package com.br.estudos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
//é utilizada pra localizar automaticamente classes q utilizam @ConfigurationProperties, permitindo q registre essas classes como beans
@ConfigurationPropertiesScan
public class EtlComicbookStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(EtlComicbookStoreApplication.class, args);
	}

}
