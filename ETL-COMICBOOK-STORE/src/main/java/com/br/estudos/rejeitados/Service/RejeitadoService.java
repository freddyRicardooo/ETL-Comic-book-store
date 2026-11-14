package com.br.estudos.rejeitados.Service;

import com.br.estudos.rejeitados.Repository.RejeitadoJdbcRepository;
import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.model.RegistroRejeitados;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Único lugar que sabe como gravar rejeitados. Quem quiser gravar (listener) passa por ele.
 */
@Service
@RequiredArgsConstructor
public class RejeitadoService {

    private final RejeitadoJdbcRepository rejeitadoJdbcRepository;

    // transação própria: o rejeitado não some se o chunk do batch der rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String step, FaseErro faseErro,
                          String tipoRegistro, Long linha,
                          String bruto, Throwable throwable) {

        RegistroRejeitados registro = new RegistroRejeitados(
                step,
                faseErro,
                tipoRegistro,
                linha,
                truncar(bruto, 2000),
                truncar(throwable.getMessage(), 1000),
                throwable.getClass().getSimpleName(),
                LocalDateTime.now()
        );

        rejeitadoJdbcRepository.salvar(registro);
    }

    // evita estourar o tamanho da coluna no Oracle
    private String truncar(String texto, int max) {
        if (texto == null) return null;
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}