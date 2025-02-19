package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.EmailMinDTO;
import com.devsuperior.dscatalog.dto.NewPasswordDTO;
import com.devsuperior.dscatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Criando o token para recuperação de senha e enviando por email
    @PostMapping(path = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailMinDTO dto) {
        authService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    // Criando uma nova senha ao clicar no link com token enviado por email
    @PutMapping(path = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        authService.saveNewPassowrd(dto);
        return ResponseEntity.noContent().build();
    }

}
