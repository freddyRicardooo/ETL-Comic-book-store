package com.br.estudos.importacao.tasklet;
import com.br.estudos.importacao.service.ControleArquivoService;
import com.br.estudos.importacao.service.HashArquivoService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@StepScope
public class RegistrarArquivoTasklet implements Tasklet {

    private final HashArquivoService hashArquivoService;
    private final ControleArquivoService controleArquivoService;
    private final Path caminho;

    public RegistrarArquivoTasklet(
            HashArquivoService hashService,
            ControleArquivoService controleService,
            @Value("${importacao.diretorio}") String diretorio,
            @Value("#{jobParameters['arquivoComicBook'] ?: 'ArquivoComicBookStore.txt'}")
            String arquivo) {
        this.hashArquivoService = hashService;
        this.controleArquivoService = controleService;
        this.caminho = Path.of(diretorio, arquivo);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
        String hash = hashArquivoService.calcularHash(caminho);

        // se o arquivo estiver bloqueado, lança exceção aqui e o job para
        controleArquivoService.iniciarProcessamento(
                hash,
                caminho.getFileName().toString(),
                jobExecution.getId()
        );

        // só chega aqui se o arquivo não estiver bloqueado.
        // o ControleArquivoJobListener lê essa mesma chave ("hash") no afterJob
        jobExecution.getExecutionContext().putString("hash", hash);

        return RepeatStatus.FINISHED;
    }
}