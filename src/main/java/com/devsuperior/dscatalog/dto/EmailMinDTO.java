package com.devsuperior.dscatalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailMinDTO {

    @NotBlank(message = "Campo obrigatório")
    @Email
    private String email;
}
