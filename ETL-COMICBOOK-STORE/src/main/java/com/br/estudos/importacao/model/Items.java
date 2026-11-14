package com.br.estudos.importacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
@Data
public class Items {


    private Long id;
     private String idPedido;
     private Integer seq;
     private String sku;
     private String titulo;
     private String editora;
     private Integer qtd;
     private BigDecimal precoUnitario;
     private BigDecimal valorItem;
}