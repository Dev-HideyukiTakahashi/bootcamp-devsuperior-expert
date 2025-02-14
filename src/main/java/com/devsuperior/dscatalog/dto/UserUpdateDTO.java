package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@UserUpdateValid
public class UserUpdateDTO extends UserDTO{

    private String password;
}
