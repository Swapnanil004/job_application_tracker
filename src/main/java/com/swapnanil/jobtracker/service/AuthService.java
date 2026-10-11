package com.swapnanil.jobtracker.service;

import com.swapnanil.jobtracker.dto.AuthResponse;
import com.swapnanil.jobtracker.dto.LoginRequest;
import com.swapnanil.jobtracker.dto.RegisterRequest;
import com.swapnanil.jobtracker.entity.Role;
import com.swapnanil.jobtracker.entity.User;
import com.swapnanil.jobtracker.exception.DuplicateResourceException;
import com.swapnanil.jobtracker.repository.UserRepository;
import com.swapnanil.jobtracker.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        User user = userRepository.save(
                new User(request.name().trim(), email, passwordEncoder.encode(request.password()), Role.USER));
        return AuthResponse.of(jwtService.generateToken(user.getEmail(), user.getRole().name()),
                user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmail(email).orElseThrow();
        return AuthResponse.of(jwtService.generateToken(user.getEmail(), user.getRole().name()),
                user.getEmail(), user.getRole().name());
    }
}
