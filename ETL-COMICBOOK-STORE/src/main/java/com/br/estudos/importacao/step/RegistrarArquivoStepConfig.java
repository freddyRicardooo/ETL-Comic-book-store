package com.br.estudos.importacao.step;

import com.br.estudos.importacao.tasklet.RegistrarArquivoTasklet;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RegistrarArquivoStepConfig {

    @Bean
    public Step registrarArquivoStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     RegistrarArquivoTasklet tasklet) {
        return new StepBuilder("registrarArquivoStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
