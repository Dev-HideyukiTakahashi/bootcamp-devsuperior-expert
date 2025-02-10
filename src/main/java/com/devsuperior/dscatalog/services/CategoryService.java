package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public List<CategoryDTO> findAll() {
        List<Category> entity = categoryRepository.findAll();
        return entity.stream()
                .map(cat -> modelMapper.map(cat, CategoryDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found! Id: " + id));
        return modelMapper.map(entity, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO save(CategoryDTO dto) {
        Category entity = modelMapper.map(dto, Category.class);
        entity = categoryRepository.save(entity);
        return modelMapper.map(entity, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = categoryRepository.getReferenceById(id);
            modelMapper.map(dto, entity);
            entity = categoryRepository.save(entity);
            return modelMapper.map(entity, CategoryDTO.class);
        } catch (EntityNotFoundException | MappingException e) {
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id){
        if(!categoryRepository.existsById(id))
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        try{
            categoryRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("SQL error, data integrity violation!");
        }
    }
}
