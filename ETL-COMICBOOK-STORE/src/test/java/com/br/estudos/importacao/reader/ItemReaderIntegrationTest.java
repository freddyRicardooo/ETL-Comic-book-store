package com.br.estudos.importacao.reader;

import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * TESTE DE INTEGRAÇÃO DO READER (sem banco, sem job completo).
 *
 * O que está "junto" aqui: o reader real (ArquivoVendasQuadrinhosReaderConfig) + o LineMapper
 * real (VendasQuadrinhosLineMapperConfig) + um arquivo de verdade em disco.
 * Ou seja: testa se a linha "ITEM;..." do arquivo vira um objeto Items com os campos certos.
 *
 * O reader é @StepScope e lê "importacao.diretorio" e o job parameter "arquivoComicBook":
 *  - o diretório vem do @DynamicPropertySource (apontando para uma pasta temporária);
 *  - o nome do arquivo vem do JobParameters que o getStepExecution() entrega ao step falso.
 */
@SpringJUnitConfig({
        ArquivoVendasQuadrinhosReaderConfig.class,
        VendasQuadrinhosLineMapperConfig.class,
        ItemReaderIntegrationTest.EscopoDeStepConfig.class
})
@TestExecutionListeners(listeners = StepScopeTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class ItemReaderIntegrationTest {

    private static final String NOME_ARQUIVO = "vendas-teste.txt";

    // Pasta temporária criada UMA vez para a classe toda. O arquivo é gravado antes do contexto subir.
    private static final Path DIRETORIO = criarDiretorioComArquivo();

    // Dois pedidos misturados com itens, igual ao arquivo real.
    private static Path criarDiretorioComArquivo() {
        try {
            Path dir = Files.createTempDirectory("importacao-teste");
            Files.write(dir.resolve(NOME_ARQUIVO), List.of(
                    "PEDIDO;1001;2026-03-10 14:30:00;C01;Maria Silva;maria@exemplo.com;ENTREGUE;CARTAO;79.90;12.00;0.00",
                    "ITEM;1001;1;SKU-100;Batman Ano Um;Panini;2;29.90;59.80",
                    "ITEM;1001;2;SKU-200;Watchmen;Panini;1;19.90;19.90"
            ));
            dir.toFile().deleteOnExit();
            return dir;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // Preenche ${importacao.diretorio} usado pelo @Value do reader.
    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("importacao.diretorio", DIRETORIO::toString);
    }

    @Configuration
    static class EscopoDeStepConfig {
        @Bean
        static StepScope stepScope() {
            return new StepScope();
        }
    }

    // Reader real (proxy step-scoped). O nome do campo bate com o nome do @Bean.
    @Autowired
    private FlatFileItemReader<Object> arquivoVendasQuadrinhosReader;

    // O listener chama este método para abrir o step falso, já com o job parameter do nome do arquivo.
    public StepExecution getStepExecution() {
        JobParameters parametros = new JobParametersBuilder()
                .addString("arquivoComicBook", NOME_ARQUIVO)
                .toJobParameters();
        return MetaDataInstanceFactory.createStepExecution(parametros);
    }

    // Fora de um job de verdade ninguém abre o reader, então fazemos open/close na mão.
    @Test
    void leItemDoArquivoEMapeiaTodosOsCampos() throws Exception {
        arquivoVendasQuadrinhosReader.open(new ExecutionContext());
        try {
            arquivoVendasQuadrinhosReader.read();   // 1ª linha é o PEDIDO: descartamos
            Object lido = arquivoVendasQuadrinhosReader.read();   // 2ª linha: primeiro ITEM

            Items item = assertInstanceOf(Items.class, lido);
            assertEquals("1001", item.getIdPedido());
            assertEquals(1, item.getSeq());
            assertEquals("SKU-100", item.getSku());
            assertEquals("Batman Ano Um", item.getTitulo());
            assertEquals("Panini", item.getEditora());
            assertEquals(2, item.getQtd());
            assertEquals(new BigDecimal("29.90"), item.getPrecoUnitario());
            assertEquals(new BigDecimal("59.80"), item.getValorItem());
        } finally {
            arquivoVendasQuadrinhosReader.close();
        }
    }

    // Garante que o prefixo da linha escolhe a classe certa e que o reader acaba com null.
    @Test
    void classificaPedidoEItensNaOrdemEFinalizaComNull() throws Exception {
        arquivoVendasQuadrinhosReader.open(new ExecutionContext());
        try {
            assertInstanceOf(Pedido.class, arquivoVendasQuadrinhosReader.read());
            assertInstanceOf(Items.class, arquivoVendasQuadrinhosReader.read());
            assertInstanceOf(Items.class, arquivoVendasQuadrinhosReader.read());
            assertNull(arquivoVendasQuadrinhosReader.read());   // fim do arquivo
        } finally {
            arquivoVendasQuadrinhosReader.close();
        }
    }

    // TODO (sua parte): teste equivalente para o PEDIDO. Leia a 1ª linha e confira
    // idPedido, dataHora (LocalDateTime), nome, email, status, formaPagamento, valorTotal, frete, desconto.
    // Veja os campos em Pedido.java.



    @Test
    void lePedidoDoArquivoEMapeiaTodosOsCampos() throws Exception {
        arquivoVendasQuadrinhosReader.open(new ExecutionContext());
        try {
            Object lido = arquivoVendasQuadrinhosReader.read();
            Pedido pedido = assertInstanceOf(Pedido.class, lido);

            assertEquals("1001", pedido.getIdPedido());
            assertEquals(LocalDateTime.of(2026, 3, 10, 14, 30, 0), pedido.getDataHora());
            assertEquals("C01", pedido.getIdCliente());
            assertEquals("Maria Silva", pedido.getNome());
            assertEquals("maria@exemplo.com", pedido.getEmail());
            assertEquals("ENTREGUE", pedido.getStatus());
            assertEquals("CARTAO", pedido.getFormaPagamento());
            assertEquals(new BigDecimal("79.90"), pedido.getValorTotal());
            assertEquals(new BigDecimal("12.00"), pedido.getFrete());
            assertEquals(new BigDecimal("0.00"), pedido.getDesconto());
        } finally {
            arquivoVendasQuadrinhosReader.close();
        }
    }
}
