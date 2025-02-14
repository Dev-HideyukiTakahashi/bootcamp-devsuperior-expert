package com.devsuperior.dscatalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class RoleDTO {

    private Long id;
    private String authority;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return Objects.equals(id, roleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
