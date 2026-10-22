package com.luisartr.bakery.user;

import com.luisartr.bakery.common.ConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse create(UserRequest req) {
        if (repository.findByEmailIgnoreCase(req.email()).isPresent()) {
            throw new ConflictException("Já existe um usuário com o e-mail: " + req.email());
        }
        User saved = repository.save(new User(req.name(), req.email(),
                encoder.encode(req.password()), req.role()));
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(UserResponse::from);
    }
}