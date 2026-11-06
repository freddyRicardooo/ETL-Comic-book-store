package com.br.estudos.importacao.step;



import com.br.estudos.rejeitados.ComicSkipListener;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportacaoPedidosQuadrinhosStepConfig {
    @Bean
    public Step importacaoPedidosQuadrinhosStep
            (JobRepository jobRepository,
             PlatformTransactionManager transactionManager,
             ItemReader<Object> arquivoVendasQuadrinhosReader,
             ItemProcessor<Object, Object> vendasQuadrinhosClassifierProcessor,
             ItemWriter<Object> VendasQuadrinhosWriterConfig, ComicSkipListener skipListener) {

        return new StepBuilder("stepComicBooks", jobRepository)
                .chunk(1000).transactionManager(transactionManager)
                .reader(arquivoVendasQuadrinhosReader)
                .processor(vendasQuadrinhosClassifierProcessor)
                .writer(VendasQuadrinhosWriterConfig)
                .faultTolerant() //modo tolerante a falhas. Sem ele a primeira linha ruim derruba o job
                .processorNonTransactional() //processor não é transacional guarde o resultado e não reprocesse
                .skip(FlatFileParseException.class) //permite pular linhas que o reader não conseguiu ler
                .skip(DataIntegrityViolationException.class) //permite pular itens q o banco recusou
                .skip(ValidationException.class)
                .skipLimit(30000) //maximo de itens pulados no step INTEIRO. Passou de 100 o job falha
                .listener(skipListener) //registra o listener. daqui em diante toda vez q o batch pular um item chama o ComicSkipListener
                .build();
    }
}
