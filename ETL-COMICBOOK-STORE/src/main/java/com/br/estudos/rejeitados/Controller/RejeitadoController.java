package com.br.estudos.rejeitados.Controller;


import com.br.estudos.rejeitados.Service.RejeitadoService;
import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.model.RegistroRejeitados;
import com.br.estudos.rejeitados.model.RejeitadoResponse;
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
@RequestMapping("/Rejeitados")
@RequiredArgsConstructor
public class RejeitadoController {

    private final RejeitadoService rejeitadoService;

    @GetMapping
    //utilizada para representar uma coleção de dados que foi dividida em páginas
    public PagedModel<RejeitadoResponse> listarRejeitados(
            @RequestParam(required = false) FaseErro fase,
            @RequestParam(required = false) String tipo,
            @PageableDefault(size = 50) Pageable pageable
    ){
        Page<RegistroRejeitados> pagina = rejeitadoService.consultar(fase, tipo, pageable);
        Page<RejeitadoResponse> paginaDto = pagina.map(this::converter);
        return new PagedModel<>(paginaDto);
    }

    private RejeitadoResponse converter(RegistroRejeitados registroRejeitados) {
        return new RejeitadoResponse(
                registroRejeitados.getId(),
                registroRejeitados.getNomeStep(),
                registroRejeitados.getFase().name(),
                registroRejeitados.getTipoRegistro(),
                registroRejeitados.getNumeroLinha(),
                registroRejeitados.getMensagemErro(),
                registroRejeitados.getDataHora());
    }
}
