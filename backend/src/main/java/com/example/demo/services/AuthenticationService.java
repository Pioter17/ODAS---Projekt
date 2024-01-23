package com.example.demo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.demo.models.User;
import com.example.demo.other.*;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuthenticationManager authenticationManager;


    public ServiceResponse<String> register(RegisterRequest request) {
        if (!Objects.equals(request.getPassword(), request.getRepeatedPassword())){
            return null;
        }
        var user = new User(request.getName(), passwordEncoder.encode(request.getPassword()), Role.USER);

        if (repository.findByName(user.getUsername()).isPresent()){
            return new ServiceResponse<>("", false, "");
        }

        String photo;
        try {
            photo = generateQRCode(user);
        } catch (Exception e) {
            photo ="";
        }

        repository.save(user);
        return new ServiceResponse<>(photo, true, "Zarejestrowano");
    }

    public ServiceResponse<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        String ipAddress = loginAttemptService.getClientIp();
        loginAttemptService.loginAttempt(ipAddress);
        if (loginAttemptService.isBlocked()){
           return new ServiceResponse<>(null, false, "Przekroczono limit prób, spróbuj za tydzień");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getName(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return new ServiceResponse<>(null, false, "Złe hasło lub loginwwwwwwwwwwwwwwww");
        }
        var user = repository.findByName(request.getName());
        if (user.isEmpty()){
            return new ServiceResponse<>(null, false, "Złe hasło lub logineeeeeeeeeeeeeeee");
        }
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("send code")
                .build();
        return new ServiceResponse<>(response, true, "Token");
    }

    public ServiceResponse<AuthenticationResponse> verifyCode(VerificationRequest verificationRequest) {
        System.out.println("dupa");
        User user = (User) loadUserByUsername(verificationRequest.getName());
        System.out.println("dupa " + user);
        Totp totp = new Totp(user.getSecret());
        System.out.println("dupa otp " + totp);
        if ( !totp.verify(verificationRequest.getCode()) || !isCodeValid(verificationRequest.getCode())){
            System.out.println("dupa1");
            throw new BadCredentialsException("Incorrect authorization data");
        }
        System.out.println("dupa2");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(verificationRequest.getName(),verificationRequest.getPassword())
        );
        System.out.println("dupa3");
        String jwtToken = jwtService.generateToken(user);
        waitSomeTime();
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
        return new ServiceResponse<>(response, true, "Zalogowano");

    }

    public void waitSomeTime() {
        Random random = new SecureRandom();
        try {
            // Generate a random wait time between 200 and 600 milliseconds
            int randomWaitTime = random.nextInt(401) + 200; // Generates a random number between 200 and 600
            Thread.sleep(randomWaitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public String generateQRCode(User user) throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(generateQRImage(user),"png", os);

        return Base64.getEncoder().encodeToString(os.toByteArray());
    }
    private UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if( loginAttemptService.isBlocked()){
            throw new RuntimeException("IP blocked wait 7 day to try again you bad hacker XD");
        }
        return repository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("Incorrect authorization data"));
    }

    private BufferedImage generateQRImage(User user) throws Exception{
        String text = "otpauth://totp/odasProjekt:"+user.getUsername()+"?secret="+user.getSecret()+"&issuer=odasProjekt";
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private boolean isCodeValid(String code){
        try{
            Long.parseLong(code);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
}
