package com.br.estudos.importacao.listener;

import com.br.estudos.importacao.service.ControleArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControleArquivoJobListener implements JobExecutionListener {

    private final ControleArquivoService controleArquivoService;

    @Override
    public void afterJob(JobExecution jobExecution) {

        String hash = jobExecution.getExecutionContext().getString("hash", null);

        if (hash == null) {
            return; // job nunca assumiu o arquivo, nada a ataulizar
        }

        long id = jobExecution.getId();

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            controleArquivoService.marcarComoProcessado(hash, id);
        }else {
            String mensagem = jobExecution.getAllFailureExceptions().stream()
                    .findFirst()  //retorna o primeiro elemento encontrado no fluxo de dados
                    .map(Throwable::getMessage)
                    .orElse("Job fonalizou com sucesso " + jobExecution.getStatus());
            controleArquivoService.marcarComoErro(hash, id, truncar(mensagem, 4000));
        }
    }

    //verificar se o texto passou do tamanho permitido
    private String truncar(String texto, int max) {
        if (texto == null) return null;
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
