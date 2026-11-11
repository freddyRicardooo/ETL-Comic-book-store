package com.br.estudos.rejeitados.Service;


import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.model.RegistroRejeitados;
import com.br.estudos.rejeitados.Repository.RegistroRejeitadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Aqui representa o unico lugar que sabe como guardar e buscar rejeitados. quem quiser gravar ou consultar
 * (listener ou controller) passa por ele
 */

@Service
@RequiredArgsConstructor
public class RejeitadoService {
    private final RegistroRejeitadoRepository registroRejeitadoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) //abre uma transação própria, então o rejeitado não some se o chunk do batch der rollback.
    public void registrar
            (String step, FaseErro faseErro,
             String tipoRegistro, Integer linha,
             String bruto, Throwable throwable){

        RegistroRejeitados rs = new  RegistroRejeitados();
        rs.setNomeStep(step);
        rs.setFase(faseErro);
        rs.setTipoRegistro(tipoRegistro);
        rs.setNumeroLinha(linha);
        rs.setConteudoBruto(truncar(bruto, 2000));
        rs.setMensagemErro(truncar(throwable.getMessage(), 1000));
        rs.setTipoExcecao(throwable.getClass().getSimpleName());
        rs.setDataHora(LocalDateTime.now());
        registroRejeitadoRepository.save(rs);
    }


    @Transactional(readOnly = true)
    public Page<RegistroRejeitados> consultar(FaseErro faseErro, String tipo, Pageable pageable){
        if (faseErro != null && tipo != null) {
            return registroRejeitadoRepository.findByFaseAndTipoRegistro(faseErro, tipo, pageable);
        } else if (faseErro != null) {
            return registroRejeitadoRepository.findByFase(faseErro, pageable);
        }else if (tipo != null) {
            return registroRejeitadoRepository.findByTipoRegistro(tipo, pageable);
        }else {
            return registroRejeitadoRepository.findAll(pageable);
        }
    }

    //evitar estourar tamanho da coluna no oracle
    public String truncar(String texto, int max){
        if (texto == null) return null;
        return texto.length() <= max ? texto : texto.substring(0, max); //se texto for <= tam de max retorna texto se não retorna os primeiros max caracteres
    }
}
