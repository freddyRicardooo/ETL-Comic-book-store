package com.br.estudos.writters;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class VendasQuadrinhosWriterConfig {
    @Bean
    public ItemWriter<Object> vendasQuadrinhosConsoleWriter() {
        return chunk -> chunk.getItems().forEach(System.out::println);
    }
}
