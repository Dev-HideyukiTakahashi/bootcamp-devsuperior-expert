package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long notExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        notExistingId = 1000L;
        countTotalProducts = 25;
    }

    @Test
    public void delete_Should_deleteObject_When_idExists() {

        productRepository.deleteById(existingId);

        Optional<Product> result = productRepository.findById(existingId);
        assertFalse(result.isPresent());
    }

    @Test
    public void save_Should_persistWithAutoincrement_When_idIsNull() {

        Product product = Factory.createProduct();
        product.setId(null);

        product = productRepository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
        assertEquals(1, product.getCategories().size());
    }

    @Test
    public void findById_Should_returnNotEmptyOptional_When_idExists() {

        Optional<Product> result = productRepository.findById(existingId);

        assertTrue(result.isPresent());
    }

    @Test
    public void findById_Should_returnEmptyOptional_When_idNotExists() {

        Optional<Product> result = productRepository.findById(notExistingId);

        assertTrue(result.isEmpty());
    }


}
