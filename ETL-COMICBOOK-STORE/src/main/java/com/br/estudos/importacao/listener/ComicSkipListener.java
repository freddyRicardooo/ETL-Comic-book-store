package com.br.estudos.importacao.listener;

import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.Service.RejeitadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;


/**
 é o ouvido do batch. Quando o spring batch pula uma linha, ele chama este listener, e o listener só repassa os dados
 pro service gravar. Nunca chama o listener: o framwork chama
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ComicSkipListener implements SkipListener<Object, Object> {
    private static final String STEP = "importacaoPedidosQuadrinhosStep";

    private final RejeitadoService rejeitadoService;

    @Override
    //dispara quando: linha elegivel, campo faltando, falha na leitura. recebe: erro
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException ex){
            String bruto = ex.getInput();//texto bruto que reader tentou converter e não conseguiu
            String tipo = extrairTipo(bruto);
            rejeitadoService.registrar(STEP, FaseErro.LEITURA, tipo, Long.valueOf(ex.getLineNumber()), bruto, t);
            registrarLog(FaseErro.LEITURA, tipo, t);
        }else { //Qualquer outra exceção que caia em onSkipInRead não tem esses dados. Não há oq preencher
            rejeitadoService.registrar(STEP, FaseErro.LEITURA, null, null, null, t);
             registrarLog(FaseErro.LEITURA, null, t);
        }
    }

    //dispara quando: processor rejeitou. recebe: item e o erro
    @Override
    public void onSkipInProcess(Object items, Throwable t) {
        String tipo = items.getClass().getSimpleName().toUpperCase();
        rejeitadoService.registrar(STEP, FaseErro.PROCESSAMENTO, tipo, null, String.valueOf(items), t);
        registrarLog(FaseErro.PROCESSAMENTO, tipo, t);
    }

    @Override
    //dispara quando: falha ao gravar no banco. recebe: item e o erro
    public void onSkipInWrite(Object item, Throwable t) {
        String tipo = item.getClass().getSimpleName().toUpperCase();
        rejeitadoService.registrar(STEP, FaseErro.ESCRITA, tipo, null, String.valueOf(item), t);
        registrarLog(FaseErro.ESCRITA, tipo, t);
    }

    private void registrarLog(FaseErro fase, String tipo, Throwable t) {

        log.debug(
                "Rejeitado fase={} tipo={} motivo={}",
                fase,
                tipo,
                t.getMessage()
        );
    }

    private String extrairTipo(String linha){
        if (linha == null || linha.isBlank()) return null;
        return linha.split(";")[0].trim(); //divide a string por ; [0] seleciona a primeira parte trim() remove os espaços das extremidades
    }
}
