package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.projections.UserDetailsProjection;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> entity = userRepository.findAll(pageable);
        return entity.map(cat -> modelMapper.map(cat, UserDTO.class));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found! Id: " + id));
        return modelMapper.map(entity, UserDTO.class);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity, dto.getPassword());
        entity.getRoles().clear();
        entity.addRole(roleRepository.findByAuthority("ROLE_OPERATOR"));
        entity = userRepository.save(entity);
        return modelMapper.map(entity, UserDTO.class);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = userRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity, dto.getPassword());
            userRepository.save(entity);
            return modelMapper.map(entity, UserDTO.class);
        } catch (EntityNotFoundException | MappingException e) {
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("Resource not found! Id: " + id);
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("SQL error, data integrity violation!");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity, String password) {
        entity.getRoles().clear();
        modelMapper.map(dto, entity);
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        }
        entity.setPassword(passwordEncoder.encode(password));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        result.forEach(projection -> user.addRole(
                new Role(projection.getRoleId(), projection.getAuthority())));

        return user;
    }
}
