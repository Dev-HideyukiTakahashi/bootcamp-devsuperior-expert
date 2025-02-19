package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.projections.ProductProjection;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> entity = productRepository.findAll(pageable);
        return entity
                .map(cat -> modelMapper.map(cat, ProductDTO.class));
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchAll(String name, String categoryId, Pageable pageable) {
        List<Long> categoryList = List.of();
        if (!categoryId.equals("0")) {
            categoryList = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();
            categoryId = "999"; //skip da condição JPQL WHERE (:categoryId = '0')
        }
        Page<ProductProjection> page = productRepository.searchAll(name, categoryList, categoryId, pageable);
        List<Long> productsId = page.map(ProductProjection::getId).toList();

        List<Product> entities = productRepository.searchProductsWithCategories(productsId, page.getSort());
        List<ProductDTO> dto = entities.stream()
                .map(prod -> modelMapper.map(prod, ProductDTO.class)).toList();

        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found! Id: " + id));
        return modelMapper.map(entity, ProductDTO.class);
    }

    @Transactional
    public ProductDTO save(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = productRepository.save(entity);
        return modelMapper.map(entity, ProductDTO.class);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);
            return modelMapper.map(entity, ProductDTO.class);
        } catch (EntityNotFoundException | MappingException e) {
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!productRepository.existsById(id))
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("SQL error, data integrity violation!");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.getCategories().clear();
        modelMapper.map(dto, entity);
        for (CategoryDTO catDTO : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDTO.getId());
            entity.getCategories().add(category);
        }
    }
}
