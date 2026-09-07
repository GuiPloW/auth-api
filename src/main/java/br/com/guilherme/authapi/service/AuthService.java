package br.com.guilherme.authapi.service;

import br.com.guilherme.authapi.dto.RegisterRequest;
import br.com.guilherme.authapi.dto.LoginRequest;
import br.com.guilherme.authapi.exception.EmailAlreadyExistsException;
import br.com.guilherme.authapi.exception.InvalidCredentialsException;
import br.com.guilherme.authapi.model.Role;
import br.com.guilherme.authapi.model.User;
import br.com.guilherme.authapi.repository.UserRepository;
import br.com.guilherme.authapi.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.com.guilherme.authapi.model.RefreshToken;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("E-mail ou senha inválidos")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos");
        }

        return user;
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    public RefreshToken createRefreshToken(User user) {
        return refreshTokenService.create(user);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public String refreshAccessToken(String token) {

        RefreshToken refreshToken = refreshTokenService.validate(token);

        User user = refreshToken.getUser();

        return jwtService.generateToken(user);
    }
}