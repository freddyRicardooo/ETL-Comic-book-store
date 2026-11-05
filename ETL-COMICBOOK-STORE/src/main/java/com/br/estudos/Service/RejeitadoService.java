package com.br.estudos.Service;


import com.br.estudos.Dominio.FaseErro;
import com.br.estudos.Repository.RegistroRejeitadoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class RejeitadoService {
    private final RegistroRejeitadoRepository registroRejeitadoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar (String step, FaseErro faseErro, String tipoRegistro, Integer linha, String bruto, Throwable throwable){

    }
}
