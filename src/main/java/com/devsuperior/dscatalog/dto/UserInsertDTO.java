package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@UserInsertValid
public class UserInsertDTO extends UserDTO{

    private String password;
}
