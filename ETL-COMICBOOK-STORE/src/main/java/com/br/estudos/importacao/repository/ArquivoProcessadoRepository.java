package com.br.estudos.importacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArquivoProcessadoRepository extends JpaRepository<ArquivoProcessado, Integer> {
    Optional<ArquivoProcessado> findByHashSha256(String hashSha256);
    Optional<ArquivoProcessado> findByJobExecutionById (Long jobExecutionId);
}
