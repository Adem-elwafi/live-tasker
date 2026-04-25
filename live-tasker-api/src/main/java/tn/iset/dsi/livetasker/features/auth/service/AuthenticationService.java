package tn.iset.dsi.livetasker.features.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.iset.dsi.livetasker.features.auth.User;
import tn.iset.dsi.livetasker.features.auth.UserRepository;
import tn.iset.dsi.livetasker.features.auth.dto.*;
import tn.iset.dsi.livetasker.features.auth.utils.JwtService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        repository.save(user);

        // Note: We need to wrap our User in a Spring Security UserDetails later
        // For now, we use the username to generate the token
        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), java.util.Collections.emptyList()));

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var user = repository.findByUsername(request.username())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), java.util.Collections.emptyList()));

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}