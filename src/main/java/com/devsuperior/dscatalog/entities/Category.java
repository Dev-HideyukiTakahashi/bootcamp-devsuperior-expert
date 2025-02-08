package com.devsuperior.dscatalog.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "tb_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
