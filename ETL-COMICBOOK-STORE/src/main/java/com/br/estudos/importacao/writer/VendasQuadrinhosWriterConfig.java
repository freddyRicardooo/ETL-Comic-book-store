package com.br.estudos.importacao.writer;

import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.Pedido;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class VendasQuadrinhosWriterConfig {

    @Bean
    public JdbcBatchItemWriter<Pedido> pedidoJdbcWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Pedido>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO PEDIDO (ID_PEDIDO, DATA_HORA, ID_CLIENTE, NOME, EMAIL,
                                        STATUS, FORMA_PAGAMENTO, VALOR_TOTAL, FRETE, DESCONTO)
                    VALUES (:idPedido, :dataHora, :idCliente, :nome, :email,
                            :status, :formaPagamento, :valorTotal, :frete, :desconto)
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Items> itemJdbcWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Items>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO ITEM_PEDIDO (ID_PEDIDO, SEQ, SKU, TITULO, EDITORA,
                                             QTD, PRECO_UNITARIO, VALOR_ITEM)
                    VALUES (:idPedido, :seq, :sku, :titulo, :editora,
                            :qtd, :precoUnitario, :valorItem)
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    public ItemWriter<Object> vendasQuadrinhosDatabaseWriter(
            JdbcBatchItemWriter<Pedido> pedidoJdbcWriter,
            JdbcBatchItemWriter<Items> itemJdbcWriter) {

        return chunk -> {
            List<Pedido> pedidos = new ArrayList<>();
            List<Items> itens = new ArrayList<>();

            for (Object item : chunk.getItems()) {
                if (item instanceof Pedido pedido) {
                    pedidos.add(pedido);
                } else if (item instanceof Items i) {
                    itens.add(i);
                } else {
                    throw new IllegalArgumentException(
                            "Tipo não suportado no writer: " + item.getClass().getName());
                }
            }

            // pedidos primeiro: o item tem FK pra pedido
            if (!pedidos.isEmpty()) {
                pedidoJdbcWriter.write(new Chunk<>(pedidos));
            }
            if (!itens.isEmpty()) {
                itemJdbcWriter.write(new Chunk<>(itens));
            }
        };
    }
}