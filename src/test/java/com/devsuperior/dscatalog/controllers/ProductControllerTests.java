package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper jsonMapper; // para converter objeto para json

    private PageImpl<ProductDTO> page;
    private ProductDTO dto;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() {

        // iniciando variaveis
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        dto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(dto));


        // simulando service
        when(service.findAll(any())).thenReturn(page);
        when(service.findById(existingId)).thenReturn(dto);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        // any() só funciona com eq() no primeiro parametro
        when(service.update(eq(existingId), any())).thenReturn(dto);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        doNothing().when(service).deleteById(existingId);
        doThrow(ResourceNotFoundException.class).when(service).deleteById(nonExistingId);
        doThrow(DatabaseException.class).when(service).deleteById(dependentId);
        when(service.save(any())).thenReturn(dto);
    }

    @Test
    public void delete_Should_returnStatus204_When_idExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_Should_returnStatus404_When_idNonExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insert_Should_returnStatus201AndProductDto() throws Exception {

        String jsonBody = jsonMapper.writeValueAsString(dto);

        mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findAll_Should_returnPage() throws Exception {

        // static import MockMvcRequestBuilders
        ResultActions result = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        // Poderia ser tudo encadeado direto, facilitando a legibilidade
        result.andExpect(status().isOk()); // static import MockMvcResultMatchers
    }

    @Test
    public void findById_Should_returnProductDto_When_idExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists()) // $ acessa o payload da response
                .andExpect(jsonPath("$.name").exists()) // testando apenas se existe o campo
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findById_Should_returnStatus404_When_idNonExists() throws Exception {
        mockMvc.perform(get("/products{id}", nonExistingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_Should_returnProductDto_When_idExists() throws Exception {

        String jsonBody = jsonMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // corpo da request
                        .contentType(MediaType.APPLICATION_JSON) // tipo da request
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void update_Should_returnStatus404_When_idNonExists() throws Exception {

        String jsonBody = jsonMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody) // corpo da request
                        .contentType(MediaType.APPLICATION_JSON) // tipo da request
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
