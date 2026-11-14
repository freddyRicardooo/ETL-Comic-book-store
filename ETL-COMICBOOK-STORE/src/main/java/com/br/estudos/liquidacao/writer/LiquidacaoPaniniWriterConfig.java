package com.br.estudos.liquidacao.writer;

import com.br.estudos.liquidacao.model.LiquidacaoPanini;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
public class LiquidacaoPaniniWriterConfig {

    @Bean
    public JdbcBatchItemWriter<LiquidacaoPanini> liquidacaoPaniniWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LiquidacaoPanini>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO LIQUIDACAO_PANINI
                            (ITEM_PEDIDO_ID, EDITORA, TITULO, FORMA_PAGAMENTO, QTD,
                             PRECO_UNITARIO, VALOR_BRUTO, PERCENTUAL_LOJA, VALOR_LOJA,
                             VALOR_EDITORA, STATUS_LIQUIDACAO, DATA_VENDA, DATA_LIQUIDACAO)
                        VALUES
                            (:itemPedidoId, :editora, :titulo, :formaPagamento, :qtd,
                             :precoUnitario, :valorBruto, :percentualLoja, :valorLoja,
                             :valorEditora, :statusLiquidacao, :dataVenda, :dataLiquidacao)
                        """)
                .beanMapped()
                .build();
    }
}

