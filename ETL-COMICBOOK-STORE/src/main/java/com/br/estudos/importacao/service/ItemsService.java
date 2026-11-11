package com.br.estudos.importacao.service;


import com.br.estudos.importacao.model.Items;
import com.br.estudos.importacao.repository.ItensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItensRepository itensRepository;

    @Transactional(readOnly = true)
    public Page<Items> consultar(String editora, String titulo, Pageable pageable) {
        if (titulo != null && titulo.trim().length() > 0) {
            return itensRepository.findByTitulo(titulo, pageable);
        } else if (editora != null && editora.trim().length() > 0) {
            return itensRepository.findByEditora(editora, pageable);
        }else {
            return itensRepository.findAll(pageable);
        }

    }
}
