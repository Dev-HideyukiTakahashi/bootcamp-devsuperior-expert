package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// IT = Integration Test
// Teste de integracao busca os dados reais do banco de dados para testar
@SpringBootTest // carregando o contexto da aplicacao
@AutoConfigureMockMvc // trata as requisicoes sem subir o servidor
@Transactional // para dar um rollback no bd apos cada teste, cada teste sera isolado e independente
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper jsonMapper; // para converter objeto para json

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findAll_Should_returnSortedPage_When_sortByName() throws Exception {

        // import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
        mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
                .andExpect(jsonPath("$.totalElements").value(countTotalProducts))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].name").value("Macbook Pro"))
                .andExpect(jsonPath("$.content[1].name").value("PC Gamer"))
                .andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void update_Should_returnProductDto_When_idExists() throws Exception {

        ProductDTO dto = Factory.createProductDTO();
        String jsonBody = jsonMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // corpo da request
                        .contentType(MediaType.APPLICATION_JSON) // tipo da request
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.description").value(dto.getDescription()));
    }

    @Test
    public void update_Should_throwStatus404_When_idNonExists() throws Exception {

        ProductDTO dto = Factory.createProductDTO();
        String jsonBody = jsonMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody) // corpo da request
                        .contentType(MediaType.APPLICATION_JSON) // tipo da request
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
