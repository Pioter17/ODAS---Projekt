package com.example.demo.controllers;

import com.example.demo.other.AuthenticationRequest;
import com.example.demo.other.AuthenticationResponse;
import com.example.demo.other.RegisterRequest;
import com.example.demo.other.ServiceResponse;
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
    public ResponseEntity<ServiceResponse<Boolean>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>(false, false, "Bad data"));
        }
        var response = service.register(request);
        if (response != null){
            service.waitSomeTime();
            return ResponseEntity.ok(response);
        } else {
            service.waitSomeTime();
            return ResponseEntity.badRequest().body(new ServiceResponse<>(false, false, "Error occured"));
        }
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ServiceResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest loginRequest,
            HttpServletRequest request
    ){
        var response = service.authenticate(loginRequest, request);
        if (response.data != null){
            service.waitSomeTime();
            return ResponseEntity.ok(response);
        } else {
            service.waitSomeTime();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
