package com.br.estudos.Dominio;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table (name = "REGISTRO_REJEITADO")
@Data
public class RegistroRejeitados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeStep;

    @Enumerated(EnumType.STRING)
    private FaseErro fase;

    private String tipoRegistro;   // PEDIDO ou ITEM

    private Integer numeroLinha;

    @Column(length = 2000)
    private String conteudoBruto;

    @Column(length = 1000)
    private String mensagemErro;

    private String tipoExcecao;

    private LocalDateTime dataHora;
}
