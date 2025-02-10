package com.devsuperior.dscatalog.config;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    // ModelMapper faz a copia entidade/dto e vice versa
    // atraves dos getter e setters
    // obs: incluir setter nas listas tambem para funcionar
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        /*
         Ignorando o 'setId()' entidade category na copia dto/entity
         evitando o erro:
         com.devsuperior.dscatalog.entities.Category was altered from null to 1
         */
        modelMapper.createTypeMap(CategoryDTO.class, Category.class)
                .addMappings(mapper -> mapper.skip(Category::setId));

        return modelMapper;
    }
}
