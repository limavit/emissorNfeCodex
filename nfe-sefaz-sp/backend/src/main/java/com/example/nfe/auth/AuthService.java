package com.example.nfe.auth;

import com.example.nfe.audit.AuditService;
import com.example.nfe.security.CurrentUser;
import com.example.nfe.security.JwtService;
import com.example.nfe.user.User;
import com.example.nfe.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService, AuditService auditService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Transactional
    public AuthController.AuthResponse register(AuthController.RegisterRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail ja cadastrado.");
        }
        User user = users.save(new User(request.name(), request.email(), passwordEncoder.encode(request.password())));
        String token = jwtService.issue(user.getId(), user.getEmail());
        auditService.register(user.getId(), null, "REGISTER", "User", user.getId().toString());
        return new AuthController.AuthResponse(token, new AuthController.MeResponse(user.getId().toString(), user.getName(), user.getEmail()));
    }

    public AuthController.AuthResponse login(AuthController.LoginRequest request) {
        User user = users.findByEmail(request.email()).orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais invalidas.");
        }
        auditService.register(user.getId(), null, "LOGIN", "User", user.getId().toString());
        return new AuthController.AuthResponse(jwtService.issue(user.getId(), user.getEmail()),
                new AuthController.MeResponse(user.getId().toString(), user.getName(), user.getEmail()));
    }

    public AuthController.MeResponse me() {
        User user = users.findById(CurrentUser.id()).orElseThrow();
        return new AuthController.MeResponse(user.getId().toString(), user.getName(), user.getEmail());
    }
}
