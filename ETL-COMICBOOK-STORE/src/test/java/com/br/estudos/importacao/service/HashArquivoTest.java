package com.br.estudos.importacao.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashArquivoTest {

    @TempDir //disponibiliza um diretório temporário para os testes
    Path diretorioTemporario;

    private final HashArquivoService hashArquivoService = new HashArquivoService();

    @Test
    void deveGerarMesmoHashParaArquivosComMesmoConteudo() throws IOException {

        Path arquivo1 = diretorioTemporario.resolve("arquivo1.txt");
        Path arquivo2 = diretorioTemporario.resolve("arquivo2.txt");

        Files.writeString(arquivo1, "ComicBookStore");
        Files.writeString(arquivo2, "ComicBookStore");

        String hash1 = hashArquivoService.calcularHash(arquivo1);
        String hash2 = hashArquivoService.calcularHash(arquivo2);

        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 1: " + hash2);
        assertEquals(hash1, hash2);
    }


    @Test
    void deveGerarHashDiferentesParaConteudosDiferentes() throws IOException {


        Path arquivo1 =  diretorioTemporario.resolve("arquivo1.txt");
        Path arquivo2 =  diretorioTemporario.resolve("arquivo2.txt");

        Files.writeString(arquivo1, "ComicBook Store");
        Files.writeString(arquivo2, "Marvel Store");

        String hash1 = hashArquivoService.calcularHash(arquivo1);
        String hash2 = hashArquivoService.calcularHash(arquivo2);
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);

    }


    @Test
    void deveGerarHashCom64Caracteres()
            throws IOException {

        Path arquivo = diretorioTemporario.resolve("arquivo.txt");

        Files.writeString(arquivo, "ComicBook Store");

        String hash = hashArquivoService.calcularHash(arquivo);


        assertEquals(64, hash.length());
    }
}

