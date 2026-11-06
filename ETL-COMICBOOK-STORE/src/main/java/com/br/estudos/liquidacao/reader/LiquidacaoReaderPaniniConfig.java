package com.br.estudos.liquidacao.reader;


import com.br.estudos.liquidacao.model.ItemLiquidacaoLeitura;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;

import static com.br.estudos.liquidacao.sql.ConsultasLiquidacao.ITENS_PANINI_ENTREGUES;


@Configuration
public class LiquidacaoReaderPaniniConfig {
    @Bean
    public JdbcCursorItemReader<ItemLiquidacaoLeitura> liquidacaoLeituraJdbcCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<ItemLiquidacaoLeitura>()
                .name("liquidacaoLeituraJdbcCursorItemReader")
                .dataSource(dataSource)
                .sql(ITENS_PANINI_ENTREGUES)
                .rowMapper(new DataClassRowMapper<>(ItemLiquidacaoLeitura.class)) // converte cada linha em objeto, ligando coluna -> campo pelo nome
                .fetchSize(500) // linhas trazidas do banco por viagem (padrão do Oracle é 10)
                .build();
    }
}
