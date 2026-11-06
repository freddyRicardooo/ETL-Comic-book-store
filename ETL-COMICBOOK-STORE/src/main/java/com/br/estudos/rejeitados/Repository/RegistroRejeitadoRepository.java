package com.br.estudos.rejeitados.Repository;

import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.model.RegistroRejeitados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRejeitadoRepository extends JpaRepository<RegistroRejeitados, Long> {
    List<RegistroRejeitados> findByFase(FaseErro faseErro);
    List<Object> findByTipoRegistro(String tipoRegistro);
    List<RegistroRejeitados> findByFaseAndTipoRegistro(FaseErro faseErro, String tipoRegistro);




}
