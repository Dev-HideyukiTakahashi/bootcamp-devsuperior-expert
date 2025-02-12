package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Testes de unidade
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO dto;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        dto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));


        //delete
        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(nonExistingId);

        //find
        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);
        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).findById(nonExistingId);

        //save
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
    }

    @Test
    public void findAll_Should_returnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findAll(pageable);
        assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findById_Should_returnProductDto_When_idExists() {
        ProductDTO dto = service.findById(existingId);
        assertNotNull(dto);
        Mockito.verify(productRepository).findById(existingId); // Verificar se o repositório foi chamado corretamente
        Mockito.verify(modelMapper).map(product, ProductDTO.class); // Verificar se o ModelMapper foi chamado
    }

    @Test
    public void findById_Should_throwResourceNotFoundException_When_idNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void update_Should_returnProductDto_When_idExists() {
        ProductDTO result = service.update(existingId, dto);
        assertNotNull(result);
    }

    @Test
    public void update_Should_throwResourceNotFoundException_When_idNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dto);
        });
    }

    @Test
    public void delete_Should_doNothing_When_idExists() {
        assertDoesNotThrow(() -> {
            service.deleteById(existingId);
        });
    }

    @Test
    public void delete_Should_throwResourceNotFoundException_When_idNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteById(nonExistingId);
        });
    }

    @Test
    public void delete_Should_throwDatabaseException_When_dependentId() {
        assertThrows(DatabaseException.class, () -> {
            service.deleteById(dependentId);
        });
    }
}
