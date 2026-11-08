package com.br.estudos.rejeitados.model;
import java.time.LocalDateTime;

public record RejeitadoResponse(
        Long id, String nomeStep, String fase, String tipoRegistro,
        Integer numeroLinha, String mensagemErro, LocalDateTime dataHora) { }
