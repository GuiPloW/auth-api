package br.com.guilherme.authapi.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}