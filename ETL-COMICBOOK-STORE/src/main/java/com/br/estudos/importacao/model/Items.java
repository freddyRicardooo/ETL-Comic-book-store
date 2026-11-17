package com.br.estudos.importacao.model;


import lombok.Data;


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