package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import org.modelmapper.ModelMapper;

import java.time.Instant;

public class Factory {


    private static ModelMapper mapper;

    public Factory(ModelMapper mapper) {
        Factory.mapper = mapper;
    }

    public static Product createProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Phone")
                .description("Good Phone")
                .price(800.0)
                .imgUrl("https://img.com/img.png")
                .date(Instant.parse("2020-10-20T03:00:00Z"))
                .build();

        Category category = Category.builder()
                .id(2L)
                .name("Electronics")
                .build();

        product.getCategories().add(category);
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return mapper.map(product, ProductDTO.class);
    }

}
