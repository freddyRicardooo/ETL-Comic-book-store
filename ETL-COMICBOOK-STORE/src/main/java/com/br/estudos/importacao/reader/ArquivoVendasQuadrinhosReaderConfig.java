package com.br.estudos.importacao.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ArquivoVendasQuadrinhosReaderConfig {

    @StepScope
    @Bean
    public FlatFileItemReader arquivoVendasQuadrinhosReader(
            @Value("#{jobParameters['arquivoComicBook'] ?: 'file:files/ArquivoComicBookStore.txt'}")
            Resource arquivoVendasQuadrinhosReader,
            LineMapper lineMapper) {
        return new FlatFileItemReaderBuilder()
                .name("arquivoVendasQuadrinhosReader")
                .resource(arquivoVendasQuadrinhosReader)
                .lineMapper(lineMapper)
                .build();
    }
}