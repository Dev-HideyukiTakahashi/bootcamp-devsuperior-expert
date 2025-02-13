package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


// IT = Integration Test
// Teste de integracao busca os dados reais do banco de dados para testar
@SpringBootTest // carregando o contexto da aplicacao
@Transactional // para dar um rollback no bd apos cada teste, cada teste sera isolado e independente
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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
    public void findAll_Should_returnProductPage() {
        Page<ProductDTO> result = productService.findAll(PageRequest.of(0,10));

        assertFalse(result.isEmpty());
        assertEquals(0, result.getNumber()); // testando pagina = 0
        assertEquals(10, result.getSize()); // testando size = 10
        assertEquals(countTotalProducts, result.getTotalElements()); // testando result = 25
    }

    @Test
    public void findAll_Should_returnEmptyPage_When_pageDoesNotExist() {
        Page<ProductDTO> result = productService.findAll(PageRequest.of(50,10));

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAll_Should_returnSortedPage_When_SortByName() {
        Page<ProductDTO> result =
                productService.findAll(PageRequest.of(0,10, Sort.by("name")));

        assertFalse(result.isEmpty());
        assertEquals("Macbook Pro", result.getContent().get(0).getName());
        assertEquals("PC Gamer", result.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

    @Test
    public void delete_Should_deleteResource_When_idExists() {

        productService.deleteById(existingId);

        assertEquals(countTotalProducts - 1, productRepository.count());
    }

    @Test
    public void delete_Should_throwResourceNotFoundException_When_idNonExists() {

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteById(nonExistingId);
        });
    }
}
