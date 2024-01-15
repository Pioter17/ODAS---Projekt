package com.example.demo.controllers;

import com.example.demo.other.AuthenticationRequest;
import com.example.demo.other.AuthenticationResponse;
import com.example.demo.other.RegisterRequest;
import com.example.demo.other.ServiceResponse;
import com.example.demo.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request
    ){
        var response = service.register(request);
        if (response != null){
            return ResponseEntity.ok(new ServiceResponse<>(response, true, "User registered"));
        } else {
            return ResponseEntity.ok(new ServiceResponse<>(null, false, "Error occured"));
        }
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        var response = service.authenticate(request);
        if (response != null){
            return ResponseEntity.ok(new ServiceResponse<>(response, true, "User authenticated"));
        } else {
            return ResponseEntity.ok(new ServiceResponse<>(null, false, "Error occured"));
        }
    }
}
