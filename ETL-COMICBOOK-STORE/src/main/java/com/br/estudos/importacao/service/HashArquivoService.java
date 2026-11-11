package com.br.estudos.importacao.service;


import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;


@Service
public class HashArquivoService {
    public String calcularHash(Path caminhoArquivo) throws IOException {
        try{

            //solicitando implementação desse algoritimo, digest será resposavel por receber os bytes do arquivo e calcular o resumo criptografado
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //Files.newInputStream(caminhoArquivo) abre o arquivo pra leitura. InputStream fluxo de bytes
            try (InputStream input = Files.newInputStream(caminhoArquivo))  {

                byte [] buffer = new byte[8192];
                int bytesLidos;

                while ((bytesLidos = input.read(buffer)) != -1){
                    md.update(buffer, 0, bytesLidos);
                }
            }

            return HexFormat.of().formatHex(md.digest());
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("Algorito SHA-256 não encontrado");
        }
    }
}
