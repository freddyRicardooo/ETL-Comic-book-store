package com.br.estudos.importacao.processor;

import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.Pedido;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class VendasQuadrinhosValidacaoProcessorConfig {

    // Teto do frete
    private static final BigDecimal FRETE_MAXIMO = new BigDecimal("20.00");


    @Bean
    @StepScope
    public ItemProcessor<Object, Object> vendasQuadrinhosClassifierProcessor(
            ItemProcessor<Pedido, Pedido> pedidoValidacaoProcessor,
            ItemProcessor<Items, Items> itemValidacaoProcessor) {

        Classifier<Object, ItemProcessor<?, ?>> classifier = objeto -> switch (objeto) {
            case Pedido pedido -> pedidoValidacaoProcessor;
            case Items items -> itemValidacaoProcessor;
            default -> throw new IllegalArgumentException(
                    "Tipo não suportado: " + objeto.getClass().getName());
        };

        //classifica se é item ou pedido
        ClassifierCompositeItemProcessor<Object, Object> processor = new ClassifierCompositeItemProcessor<>();
        processor.setClassifier(classifier);
        return processor;
    }

    // DELEGATE 1: só trata Pedido (duplicado + teto do frete)
    @Bean
    @StepScope
    public ItemProcessor<Pedido, Pedido> pedidoValidacaoProcessor(Set<String> pedidosAceitos) {
        Set<String> chavesProcessadas = new HashSet<>();

        ValidatingItemProcessor<Pedido> processor = new ValidatingItemProcessor<>();
        processor.setValidator(pedido -> {
            if (!chavesProcessadas.add(pedido.getIdPedido())) {
                throw new ValidationException("Pedido duplicado: " + pedido.getIdPedido());
            }
            if (pedido.getFrete() != null && pedido.getFrete().compareTo(FRETE_MAXIMO) > 0) {
                throw new ValidationException(String.format(
                        "Frete acima do teto (%s) no pedido %s: %s",
                        FRETE_MAXIMO, pedido.getIdPedido(), pedido.getFrete()));
            }
            if(pedido.getStatus().equalsIgnoreCase("COMPRA_RECUSADO") || pedido.getStatus().equalsIgnoreCase("NAO_ENTREGUE") || pedido.getStatus().equalsIgnoreCase("CANCELADA") || pedido.getStatus().equalsIgnoreCase("COMPRA_RECUSADO"))  {
                throw new ValidationException(String.format(
                        "Status de compra recusada ou não entregue: %s",
                        pedido.getStatus()
                ));
            }

            if (pedido.getFormaPagamento().equalsIgnoreCase("PIX")){
                throw new ValidationException(String.format("PIX não sera cadastrado na case de dados: %s", pedido.getFormaPagamento()));
            }

            if (pedido.getEmail().isEmpty() || pedido.getEmail() == null) {
                throw new ValidationException(String.format("Email vazio ou nulo: %s", pedido.getEmail()));
            }
            pedidosAceitos.add(pedido.getIdPedido());
        });

        return processor;
    }

    // DELEGATE 2: só trata Items (duplicado + preço zerado)
    @Bean
    @StepScope
    public ItemProcessor<Items, Items> itemValidacaoProcessor(Set <String> pedidosAceitos) {
        Set<String> chavesProcessadas = new HashSet<>();

        ValidatingItemProcessor<Items> processor = new ValidatingItemProcessor<>();
        processor.setValidator(items -> {
            String chave = items.getIdPedido() + "-" + items.getSeq();
            if (!chavesProcessadas.add(chave)) {
                throw new ValidationException("Item duplicado: " + chave);
            }
            if (items.getPrecoUnitario() == null || items.getPrecoUnitario().signum() <= 0) {
                throw new ValidationException("Preço zerado, ausente no item ou menor que 0 " + chave);
            }

            if (!pedidosAceitos.contains(items.getIdPedido())) {
                throw new ValidationException("Item sem pedido válido (pedido descartado ou ausente): " + chave);
            }

        });
        return processor;
    }



    @Bean
    public BeanValidatingItemProcessor<Object> vendasQuadrinhosValidacaoProcessorComposite(){
        BeanValidatingItemProcessor <Object> processor = new BeanValidatingItemProcessor<>();
        processor.setFilter(true);
        return processor;
    }

    @Bean
    @StepScope
    public Set<String> pedidosAceitos() {
        return ConcurrentHashMap.newKeySet();
    }
}