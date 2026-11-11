package com.br.estudos.importacao.repository;

import com.br.estudos.importacao.model.Items;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItensRepository extends JpaRepository<Items, Long> {
    Page<Items> findByEditora(String editora, Pageable pageable);
    Page<Items> findByTitulo(String titulo, Pageable pageable);
}
