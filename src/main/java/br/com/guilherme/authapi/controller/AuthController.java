package br.com.guilherme.authapi.controller;

import br.com.guilherme.authapi.dto.RegisterRequest;
import br.com.guilherme.authapi.dto.LoginRequest;
import br.com.guilherme.authapi.dto.LoginResponse;
import br.com.guilherme.authapi.model.User;
import br.com.guilherme.authapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.guilherme.authapi.dto.UserResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        User user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        User user = authService.login(request);

        String token = authService.generateToken(user);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer"
        );

        return ResponseEntity.ok(response);
    }
}