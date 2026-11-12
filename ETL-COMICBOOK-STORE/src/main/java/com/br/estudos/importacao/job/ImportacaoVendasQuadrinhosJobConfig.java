package com.br.estudos.importacao.job;

import com.br.estudos.importacao.listener.ControleArquivoJobListener;
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
public class ImportacaoVendasQuadrinhosJobConfig {

    @Bean
    public Job jobImportacao(
            JobRepository jobRepository,
            @Qualifier("registrarArquivoStep") Step stepRegistroArquivo,
            @Qualifier("importacaoPedidosQuadrinhosStep") Step stepCarga,
            @Qualifier("vendasQuadrinhasLiquidadasStep") Step stepLiquidacao,
            JobLogListener jobLogListener,
            ControleArquivoJobListener controleArquivoJobListener) {

        return new JobBuilder("jobImportacao", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobLogListener)
                .listener(controleArquivoJobListener)
                .start(stepRegistroArquivo)   // 1. calcula o hash e "assume" o arquivo
                .next(stepCarga)              // 2. importa
                .next(stepLiquidacao)         // 3. liquida
                .build();
    }
}