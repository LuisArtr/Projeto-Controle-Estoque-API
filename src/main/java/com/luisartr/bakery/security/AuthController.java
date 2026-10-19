package com.luisartr.bakery.security;

import com.luisartr.bakery.common.UnauthorizedException;
import com.luisartr.bakery.user.User;
import com.luisartr.bakery.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthController(UserRepository users, PasswordEncoder encoder, TokenService tokenService) {
        this.users = users;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        User user = users.findByEmailIgnoreCase(req.email())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos."));

        return new LoginResponse(tokenService.generate(user),
                TokenService.EXPIRES_IN_SECONDS, user.getRole());
    }
}   