package com.example.demo.services;


import com.example.demo.models.User;
import com.example.demo.other.*;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuthenticationManager authenticationManager;


    public ServiceResponse<Boolean> register(RegisterRequest request) {
        if (!Objects.equals(request.getPassword(), request.getRepeatedPassword())){
            return null;
        }
        var user = User.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        if (repository.findByName(user.getUsername()).isPresent()){
            return new ServiceResponse<>(false, false, "");
        }

        repository.save(user);
        return new ServiceResponse<>(true, true, "Zarejestrowano");
    }

    public ServiceResponse<AuthenticationResponse> authenticate(AuthenticationRequest Loginrequest, HttpServletRequest request) {
        String ipAddress = loginAttemptService.getClientIp(request);
        loginAttemptService.loginAttempt(ipAddress);
        if (loginAttemptService.isBlocked(ipAddress)){
           return new ServiceResponse<>(null, false, "Przekroczono limit prób, spróbuj za tydzień");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            Loginrequest.getName(),
                            Loginrequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return new ServiceResponse<>(null, false, "Złe hasło lub login");
        }
        var user = repository.findByName(Loginrequest.getName());
        if (user.isEmpty()){
            return new ServiceResponse<>(null, false, "Złe hasło lub login");
        }
        var jwtToken = jwtService.generateToken(user.get());
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
        return new ServiceResponse<>(response, true, "Token");
    }
}
