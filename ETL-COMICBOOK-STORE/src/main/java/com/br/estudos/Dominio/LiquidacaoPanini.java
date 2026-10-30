package com.br.estudos.Dominio;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "LIQUIDACAO_PANINI")
public class LiquidacaoPanini {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ITEM_PEDIDO_ID", nullable = false, unique = true)
    private Long itemPedidoId;

    @Column(name = "EDITORA", length = 100)
    private String editora;

    @Column(name = "TITULO", length = 150)
    private String titulo;

    @Column(name = "FORMA_PAGAMENTO", length = 30)
    private String formaPagamento;

    @Column(name = "QTD")
    private Integer qtd;

    @Column(name = "PRECO_UNITARIO", precision = 18, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "VALOR_BRUTO", precision = 18, scale = 2)
    private BigDecimal valorBruto;

    @Column(name = "PERCENTUAL_LOJA", precision = 5, scale = 2)
    private BigDecimal percentualLoja;

    @Column(name = "VALOR_LOJA", precision = 18, scale = 2) //18 digitos e 2 dps da virgula
    private BigDecimal valorLoja;

    @Column(name = "VALOR_EDITORA", precision = 18, scale = 2)
    private BigDecimal valorEditora;

    @Column(name = "STATUS_LIQUIDACAO", length = 20)
    private String statusLiquidacao;

    @Column(name = "DATA_VENDA")
    private LocalDateTime dataVenda;

    @Column(name = "DATA_LIQUIDACAO")
    private LocalDateTime dataLiquidacao;
}
