package com.br.estudos.importacao.writer;

import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE DE INTEGRAÇÃO DO WRITER (com Oracle real em container).
 *
 * O que está "junto": o writer real (vendasQuadrinhosDatabaseWriter) + os JdbcBatchItemWriter
 * + o schema criado pelo Flyway (com PK e FK de verdade) + um Oracle de verdade.
 * É o único jeito de provar que as constraints do banco (FK do item, PK do pedido) funcionam
 * como o step espera quando pula com DataIntegrityViolationException.
 *
 * Não roda o job: o teste chama writer.write(chunk) direto, sem reader/processor.
 */
@SpringBootTest(properties = "spring.batch.job.enabled=false")
@Testcontainers
class VendasQuadrinhosWriterIntegrationTest {

    // Oracle descartável. @ServiceConnection faz o Boot usar este banco no lugar do localhost:1522.
    @Container
    @ServiceConnection
    static OracleContainer oracle =
            new OracleContainer("gvenzl/oracle-free:23-slim-faststart")
                    .withStartupTimeout(Duration.ofMinutes(10));

    // Writer que o step usa. Recebe um chunk misto (Pedido + Items) e grava nas duas tabelas.
    @Autowired
    private ItemWriter<Object> vendasQuadrinhosDatabaseWriter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // O container é compartilhado pela classe, então limpamos as tabelas entre os testes.
    // Ordem importa: o filho (ITEM_PEDIDO) antes do pai (PEDIDO), por causa da FK.
    @BeforeEach
    void limpaTabelas() {
        jdbcTemplate.update("DELETE FROM ITEM_PEDIDO");
        jdbcTemplate.update("DELETE FROM PEDIDO");
    }

    private Pedido pedido(String id) {
        Pedido p = new Pedido();
        p.setIdPedido(id);
        p.setDataHora(LocalDateTime.of(2026, 3, 10, 14, 30, 0));
        p.setIdCliente("C01");
        p.setNome("Maria Silva");
        p.setEmail("maria@exemplo.com");
        p.setStatus("ENTREGUE");
        p.setFormaPagamento("CARTAO");
        p.setValorTotal(new BigDecimal("79.90"));
        p.setFrete(new BigDecimal("12.00"));
        p.setDesconto(new BigDecimal("0.00"));
        return p;
    }

    private Items item(String idPedido, int seq) {
        Items i = new Items();
        i.setIdPedido(idPedido);
        i.setSeq(seq);
        i.setSku("SKU-" + seq);
        i.setTitulo("Batman Ano Um");
        i.setEditora("Panini");
        i.setQtd(2);
        i.setPrecoUnitario(new BigDecimal("29.90"));
        i.setValorItem(new BigDecimal("59.80"));
        return i;
    }

    private int contar(String tabela) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tabela, Integer.class);
    }

    @Test
    void gravaPedidoEItensEPreservaOsValores() throws Exception {
        vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of(
                pedido("1001"), item("1001", 1), item("1001", 2))));

        assertEquals(1, contar("PEDIDO"));
        assertEquals(2, contar("ITEM_PEDIDO"));

        // Confere os valores no banco, não só a contagem (inclui BigDecimal e data).
        Map<String, Object> linha = jdbcTemplate.queryForMap(
                "SELECT NOME, VALOR_TOTAL, FRETE, DATA_HORA FROM PEDIDO WHERE ID_PEDIDO = '1001'");
        assertEquals("Maria Silva", linha.get("NOME"));
        assertEquals(0, new BigDecimal("79.90").compareTo((BigDecimal) linha.get("VALOR_TOTAL")));
        assertEquals(0, new BigDecimal("12.00").compareTo((BigDecimal) linha.get("FRETE")));
    }

    // O item vem ANTES do pedido na lista: o writer precisa gravar o pedido primeiro (FK).
    @Test
    void gravaPedidoAntesDosItensMesmoComItemNaFrente() throws Exception {
        vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of(
                item("1001", 1), pedido("1001"))));

        assertEquals(1, contar("PEDIDO"));
        assertEquals(1, contar("ITEM_PEDIDO"));
    }

    // Item cujo pedido não existe no banco: a FK recusa. O step pula isso (skip).
    @Test
    void rejeitaItemSemPedidoPorViolacaoDeFk() {
        assertThrows(DataIntegrityViolationException.class, () ->
                vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of(item("9999", 1)))));

        assertEquals(0, contar("ITEM_PEDIDO"));
    }

    // Mesmo id de pedido duas vezes: a PK recusa.
    @Test
    void rejeitaPedidoDuplicadoPorViolacaoDePk() throws Exception {
        vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of(pedido("1001"))));

        assertThrows(DataIntegrityViolationException.class, () ->
                vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of(pedido("1001")))));

        assertEquals(1, contar("PEDIDO"));
    }

    @Test
    void rejeitaTipoNaoSuportado() {
        assertThrows(IllegalArgumentException.class, () ->
                vendasQuadrinhosDatabaseWriter.write(new Chunk<>(List.of("não sou Pedido nem Item"))));
    }
}
