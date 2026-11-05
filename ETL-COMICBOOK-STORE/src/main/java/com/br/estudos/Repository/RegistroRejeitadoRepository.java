package com.br.estudos.Repository;

import com.br.estudos.Dominio.FaseErro;
import com.br.estudos.Dominio.Items;
import com.br.estudos.Dominio.Pedido;
import com.br.estudos.Dominio.RegistroRejeitados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRejeitadoRepository extends JpaRepository<RegistroRejeitados, Long> {
    List<RegistroRejeitados> findByFase(FaseErro faseErro);
    List<Object> findByTipoRegistro(String tipoRegistro);
    List<RegistroRejeitados> findByFaseAndTipoRegistro(FaseErro faseErro, String tipoRegistro);




}
