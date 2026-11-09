package com.br.estudos.liquidacao.step;

import com.br.estudos.importacao.listener.JobLogListener;
import com.br.estudos.importacao.listener.ResumoStepListener;
import com.br.estudos.liquidacao.model.ItemLiquidacaoLeitura;
import com.br.estudos.liquidacao.model.LiquidacaoPanini;
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
public class VendasQuadrinhasLiquidadasStepConfig {

    @Bean
    public Step vendasQuadrinhasLiquidadasStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<ItemLiquidacaoLeitura> liquidacaoPaniniReader,
            ItemProcessor<ItemLiquidacaoLeitura, LiquidacaoPanini> liquidacaoPaniniProcessor,
            ItemWriter<LiquidacaoPanini> liquidacaoPaniniWriter, ResumoStepListener resumoStepListener) {

        return new StepBuilder("vendasQuadrinhasLiquidadasStep", jobRepository)
                .<ItemLiquidacaoLeitura, LiquidacaoPanini>chunk(1000)
                .transactionManager(transactionManager)
                .reader(liquidacaoPaniniReader)
                .processor(liquidacaoPaniniProcessor)
                .writer(liquidacaoPaniniWriter)
                .listener(resumoStepListener)
                .build();
    }
}
