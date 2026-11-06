package com.br.estudos.importacao.writer;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class VendasQuadrinhosWriterConfig {

    @Bean
    public ItemWriter<Object> vendasQuadrinhosDatabaseWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Object>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }
}
