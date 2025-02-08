package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> entity = categoryRepository.findAll();
        return entity.stream()
                .map(cat -> modelMapper.map(cat, CategoryDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found! Id: " + id));
        return modelMapper.map(entity, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO save(CategoryDTO dto){
        Category entity = modelMapper.map(dto, Category.class);
        entity = categoryRepository.save(entity);
        return modelMapper.map(entity, CategoryDTO.class);

    }
}
