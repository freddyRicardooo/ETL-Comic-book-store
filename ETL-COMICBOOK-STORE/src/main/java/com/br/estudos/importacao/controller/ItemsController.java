package com.br.estudos.importacao.controller;


import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.model.ItemsResponse;
import com.br.estudos.importacao.service.ItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Itens")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemsService itemsService;

    @GetMapping
    public PagedModel<ItemsResponse> listarItems(
            @RequestParam(required = false) String editora,
            @RequestParam (required = false) String titulo,
            @PageableDefault (size = 50) Pageable pageable
            ){

        Page<Items> pagina = itemsService.consultar(editora, titulo, pageable);
        Page<ItemsResponse> paginaDto = pagina.map(this::converter);
        return new PagedModel<>(paginaDto);

    }

    private ItemsResponse converter(Items items) {
        return new ItemsResponse(
                items.getId(),
                items.getIdPedido(),
                items.getPedido(),
                items.getSeq(),
                items.getSku(),
                items.getTitulo(),
                items.getEditora(),
                items.getQtd(),
                items.getPrecoUnitario(),
                items.getValorItem()
        );
    }
}
