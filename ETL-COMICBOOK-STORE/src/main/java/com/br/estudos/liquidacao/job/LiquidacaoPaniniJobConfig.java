package com.br.estudos.liquidacao.job;

import com.br.estudos.importacao.listener.JobLogListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LiquidacaoPaniniJobConfig {
    @Bean
    public Job jobLiquidacap(
            JobRepository jobRepository,
            @Qualifier("vendasQuadrinhasLiquidadasStep") Step stepLiquidacao, JobLogListener jobLogListener) {
        return new JobBuilder("jobLiquidacap", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepLiquidacao)
                .listener(jobLogListener)
                .build();
    }
}
