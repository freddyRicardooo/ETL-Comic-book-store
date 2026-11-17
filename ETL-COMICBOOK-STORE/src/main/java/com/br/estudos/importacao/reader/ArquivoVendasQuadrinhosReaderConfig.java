package com.br.estudos.importacao.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import java.nio.file.Path;

@Configuration
public class ArquivoVendasQuadrinhosReaderConfig {

    @StepScope
    @Bean
    public FlatFileItemReader arquivoVendasQuadrinhosReader(
            @Value("${importacao.diretorio}") String diretorio,
            @Value("#{jobParameters['arquivoComicBook'] ?: 'ArquivoComicBookStore.txt'}")
            String ArquivoComicBookStore,
            LineMapper lineMapper) {

        Path caminho = Path.of(diretorio, ArquivoComicBookStore);
        return new FlatFileItemReaderBuilder()
                .name("arquivoVendasQuadrinhosReader")
                .resource(new FileSystemResource(caminho))
                .lineMapper(lineMapper)
                .build();
    }
}