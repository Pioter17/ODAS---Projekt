package com.example.demo.controllers;

import com.example.demo.other.*;
import com.example.demo.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<String>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>("", false, "Bad data"));
        }
        var response = service.register(request);
        if (response != null){
            service.waitSomeTime();
            return ResponseEntity.ok(response);
        } else {
            service.waitSomeTime();
            return ResponseEntity.badRequest().body(new ServiceResponse<>("", false, "Error occured"));
        }
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest loginRequest
    ){
        var response = service.authenticate(loginRequest);
        if (response.data != null){
            service.waitSomeTime();
            return ResponseEntity.ok(response);
        } else {
            service.waitSomeTime();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<AuthenticationResponse>> verify(
            @RequestBody VerificationRequest verificationRequest
            ){
        var response = service.verifyCode(verificationRequest);
        if (response.data.getToken() != null){
            service.waitSomeTime();
            return ResponseEntity.ok(response);
        } else {
            service.waitSomeTime();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
