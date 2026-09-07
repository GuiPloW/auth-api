package br.com.guilherme.authapi.dto;

import br.com.guilherme.authapi.model.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}