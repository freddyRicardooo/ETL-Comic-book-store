package com.br.estudos.writters;

import com.br.estudos.Dominio.LiquidacaoPanini;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiquidacaoPaniniWriterConfig {
    @Bean
    public ItemWriter<LiquidacaoPanini> liquidacaoPaniniWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<LiquidacaoPanini>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)   // INSERT direto, sem o SELECT prévio do merge
                .build();
    }
}

