package com.coxinha;

import com.coxinha.model.Salgado;
import com.coxinha.patterns.factory.SalgadoFactory;
import com.coxinha.repository.ClienteRepository;
import com.coxinha.repository.SalgadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClienteRepository clienteRepository, SalgadoRepository salgadoRepository) {
        return args -> {
            // Semear Salgados (usando o Factory Method)
            if (salgadoRepository.count() == 0) {
                Salgado frango = SalgadoFactory.createSalgado("FRANGO", 1000);
                Salgado carne = SalgadoFactory.createSalgado("CARNE", 1000);
                Salgado queijo = SalgadoFactory.createSalgado("QUEIJO", 1000);
                Salgado catupiry = SalgadoFactory.createSalgado("CATUPIRY", 1000);

                salgadoRepository.save(frango);
                salgadoRepository.save(carne);
                salgadoRepository.save(queijo);
                salgadoRepository.save(catupiry);
                System.out.println(">>> Banco semeado: Cardápio de coxinhas inserido usando SalgadoFactory.");
            }
        };
    }
}
