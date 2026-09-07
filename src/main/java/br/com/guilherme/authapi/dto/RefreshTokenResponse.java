package br.com.guilherme.authapi.dto;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType
) {
}