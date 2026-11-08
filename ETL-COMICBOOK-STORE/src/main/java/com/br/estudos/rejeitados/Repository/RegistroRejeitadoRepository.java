package com.br.estudos.rejeitados.Repository;

import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.model.RegistroRejeitados;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRejeitadoRepository extends JpaRepository<RegistroRejeitados, Long> {
    List<RegistroRejeitados> findByFase(FaseErro faseErro);
    List<Object> findByTipoRegistro(String tipoRegistro);
    List<RegistroRejeitados> findByFaseAndTipoRegistro(FaseErro faseErro, String tipoRegistro);
    Page<RegistroRejeitados> findByFase(FaseErro fase, Pageable pageable);
    Page<RegistroRejeitados> findByTipoRegistro(String tipoRegistro, Pageable pageable);
    Page<RegistroRejeitados> findByFaseAndTipoRegistro(FaseErro faseErro, String tipoRegistro, Pageable pageable);
}

