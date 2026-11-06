package com.br.estudos.importacao.reader;

import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.Pedido;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VendasQuadrinhosLineMapperConfig {

    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Define o mapeamento das linhas do arquivo para os objetos de domínio,
    // selecionando o tokenizer e o FieldSetMapper conforme o padrão da linha.
    @Bean
    public LineMapper<Object> lineMapper() {
        return new PatternMatchingCompositeLineMapper<>(tokenizers(), fieldSetMappers());
    }


    // Associa cada prefixo de linha ao seu respectivo LineTokenizer,
    // responsável por dividir os campos do registro.
    private Map<String, LineTokenizer> tokenizers() {
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("PEDIDO*", pedidoLineTokenizer());
        tokenizers.put("ITEM*", itemLineTokenizer());
        return tokenizers;
    }


    // Associa cada padrão de linha ao FieldSetMapper correspondente,
    // responsável por converter os campos em objetos de domínio.
    private Map<String, FieldSetMapper<Object>> fieldSetMappers() {
        Map<String, FieldSetMapper<Object>> fieldSetMappers = new HashMap<>();
        fieldSetMappers.put("PEDIDO*", fieldSetMapper(Pedido.class));
        fieldSetMappers.put("ITEM*", fieldSetMapper(Items.class));
        return fieldSetMappers;
    }


    // Configura o FieldSetMapper para instanciar a classe de domínio informada
    // e registra um conversor personalizado para valores do tipo LocalDateTime.
    private FieldSetMapper<Object> fieldSetMapper(Class<?> classe) {
        BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(classe);
        fieldSetMapper.setCustomEditors(Map.of(
                LocalDateTime.class, localDateTimeEditor()));
        return fieldSetMapper;
    }


    // Cria um conversor personalizado para transformar textos em objetos LocalDateTime,
    // utilizando o formato de data e hora definido na constante FORMATO_DATA_HORA.
    private PropertyEditor localDateTimeEditor() {
        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDateTime.parse(text.trim(), FORMATO_DATA_HORA));
                }
            }
        };
    }


    // Configura o tokenizer dos registros de pedidos,
    // definindo os nomes e as posições dos campos que serão extraídos da linha.
    private LineTokenizer pedidoLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
        lineTokenizer.setNames("idPedido", "dataHora", "idCliente", "nome", "email", "status",
                "formaPagamento", "valorTotal", "frete", "desconto");
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return lineTokenizer;
    }


    // Configura o tokenizer dos registros de itens,
    // definindo os nomes e as posições dos campos que serão extraídos da linha.
    private LineTokenizer itemLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
        lineTokenizer.setNames("idPedido", "seq", "sku", "titulo", "editora", "qtd",
                "precoUnitario", "valorItem");
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7, 8);
        return lineTokenizer;
    }
}