package com.br.estudos.Steps;


import com.br.estudos.Dominio.Items;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportacaoPedidosQuadrinhosStepConfig {
    @Bean
    public Step importacaoPedidosQuadrinhosStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Object> arquivoVendasQuadrinhosReader, ItemProcessor<Object, Object> vendasQuadrinhosClassifierProcessor, ItemWriter<Object> VendasQuadrinhosWriterConfig) {
        return new StepBuilder("stepComicBooks", jobRepository)
                .chunk(1000).transactionManager(transactionManager)
                .reader(arquivoVendasQuadrinhosReader)
                .processor(vendasQuadrinhosClassifierProcessor)
                .writer(VendasQuadrinhosWriterConfig)
                .build();
    }
}
