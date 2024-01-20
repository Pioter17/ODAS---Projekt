package com.example.demo.other;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Size(min = 5, message = "Name must have at least 5 characters")
    private String name;

    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).*$",
            message = "Password must contain at least one digit, one lowercase and one uppercase letter, and one special character")
    private String password;

    @Size(min = 8, message = "Repeated password must have at least 8 characters")
    private String repeatedPassword;

    private Role role;
}
