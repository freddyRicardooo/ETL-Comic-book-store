package com.br.estudos.importacao.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HashArquivoTest {

    // @TempDir: o JUnit cria uma pasta temporária para os testes e apaga no fim
    @TempDir
    Path diretorioTemporario;

    private final HashArquivoService hashArquivoService = new HashArquivoService();

    // @Test: marca o método como teste.
    // Dois arquivos com o mesmo conteúdo devem gerar o mesmo hash.
    @Test
    void deveGerarMesmoHashParaArquivosComMesmoConteudo() throws IOException {

        Path arquivo1 = diretorioTemporario.resolve("arquivo1.txt");
        Path arquivo2 = diretorioTemporario.resolve("arquivo2.txt");

        Files.writeString(arquivo1, "ComicBookStore");
        Files.writeString(arquivo2, "ComicBookStore");

        String hash1 = hashArquivoService.calcularHash(arquivo1);
        String hash2 = hashArquivoService.calcularHash(arquivo2);

        assertEquals(hash1, hash2);
    }


    // Conteúdos diferentes devem gerar hashes diferentes.
    @Test
    void deveGerarHashDiferentesParaConteudosDiferentes() throws IOException {


        Path arquivo1 =  diretorioTemporario.resolve("arquivo1.txt");
        Path arquivo2 =  diretorioTemporario.resolve("arquivo2.txt");

        Files.writeString(arquivo1, "ComicBook Store");
        Files.writeString(arquivo2, "Marvel Store");

        String hash1 = hashArquivoService.calcularHash(arquivo1);
        String hash2 = hashArquivoService.calcularHash(arquivo2);

        assertNotEquals(hash1, hash2);
    }

    // Compara com um valor oficial conhecido (SHA-256 de entrada vazia),
    // em vez de só comparar dois hashes entre si.
    @Test
    void deveGerarHashConhecidoParaArquivoVazio() throws IOException {

        Path arquivo = diretorioTemporario.resolve("vazio.txt");
        Files.writeString(arquivo, "");

        String hash = hashArquivoService.calcularHash(arquivo);

        // valor publicado para SHA-256 de entrada vazia
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", hash);
    }


    // SHA-256 em hexadecimal sempre tem 64 caracteres.
    @Test
    void deveGerarHashCom64Caracteres()
            throws IOException {

        Path arquivo = diretorioTemporario.resolve("arquivo.txt");

        Files.writeString(arquivo, "ComicBook Store");

        String hash = hashArquivoService.calcularHash(arquivo);


        assertEquals(64, hash.length());
    }
}
