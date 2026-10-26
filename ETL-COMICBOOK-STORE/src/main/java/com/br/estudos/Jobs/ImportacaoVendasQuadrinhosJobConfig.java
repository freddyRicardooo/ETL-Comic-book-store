package com.br.estudos.Jobs;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportacaoVendasQuadrinhosJobConfig {

    @Bean
    public Job jobComicBooks(JobRepository jobRepository, Step stepComicBooks) {
        return new JobBuilder("Jobpedidos", jobRepository)
                .start(stepComicBooks)
                .build();
    }
}
