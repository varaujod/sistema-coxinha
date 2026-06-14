package com.coxinha.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarClienteComSaldoInicial() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Novo Cliente\",\"saldoInicial\":25.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Cliente"))
                .andExpect(jsonPath("$.saldo").value(25.5));
    }

    @Test
    void deveValidarNomeObrigatorioAoCriarCliente() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"   \",\"saldoInicial\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nome")));
    }
}
