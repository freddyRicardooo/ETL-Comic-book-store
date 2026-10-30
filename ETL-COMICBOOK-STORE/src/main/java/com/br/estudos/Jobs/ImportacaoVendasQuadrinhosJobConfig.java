package com.br.estudos.Jobs;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ImportacaoVendasQuadrinhosJobConfig {

    @Bean
    public Job jobComicBooks(
            JobRepository jobRepository,
            @Qualifier("importacaoPedidosQuadrinhosStep") Step stepCarga,
            @Qualifier("vendasQuadrinhasLiquidadasStep") Step stepLiquidacao) {

        return new JobBuilder("jobComicBooks", jobRepository)
                .incrementer(new RunIdIncrementer()) // Aqui!
                .start(stepCarga)
                .next(stepLiquidacao)
                .build();
    }
}
