package com.br.estudos.Processors;

import com.br.estudos.DTO.ItemLiquidacaoLeitura;
import com.br.estudos.Dominio.LiquidacaoPanini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class LiquidacaoPaniniProcessor implements ItemProcessor<ItemLiquidacaoLeitura, LiquidacaoPanini> {
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.2");
    private static final Logger log = LoggerFactory.getLogger(LiquidacaoPaniniProcessor.class);

    @Override
    public LiquidacaoPanini process(ItemLiquidacaoLeitura item) throws Exception {
        log.info("Processando item: {}", item.itemId());

        BigDecimal bruto = item.precoUnitario()
                .multiply(BigDecimal.valueOf(item.qtd()))
                .setScale(2, BigDecimal.ROUND_HALF_UP); //duas casas dps da virgula arredondando pra cima

        if (bruto.compareTo(item.valorItem()) != 0) {
            log.warn("Item {} divergente: calculado {} x arquivo {}", item.itemId(), bruto, item.valorItem());
            return null; //batch pula o item
        }

        if (item.valorItem().signum() <= 0){
            return null;
        }

        BigDecimal valorLoja = bruto.multiply(PERCENTUAL);
        BigDecimal valorEditora = bruto.subtract(valorLoja);

        LiquidacaoPanini liquidacao = new LiquidacaoPanini();
        liquidacao.setItemPedidoId(item.itemId());
        liquidacao.setEditora(item.editora());
        liquidacao.setTitulo(item.titulo());
        liquidacao.setFormaPagamento(item.formaPagamento());
        liquidacao.setQtd(item.qtd());
        liquidacao.setPrecoUnitario(item.precoUnitario());
        liquidacao.setValorBruto(bruto);
        liquidacao.setPercentualLoja(PERCENTUAL);
        liquidacao.setValorLoja(valorLoja);
        liquidacao.setValorEditora(valorEditora);
        liquidacao.setStatusLiquidacao("LIQUIDADO");
        liquidacao.setDataVenda(item.dataHora());
        liquidacao.setDataLiquidacao(LocalDateTime.now());
        return liquidacao;
    }
}
