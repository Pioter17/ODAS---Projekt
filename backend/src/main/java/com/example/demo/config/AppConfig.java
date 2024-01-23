package com.example.demo.config;

import com.example.demo.models.Note;
import com.example.demo.other.AuthenticationResponse;
import com.example.demo.other.RegisterRequest;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.demo.other.Role.ADMIN;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner commandLineRunner(AuthenticationService service) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .name("admin")
                    .password("Admin12$")
                    .repeatedPassword("Admin12$")
                    .role(ADMIN)
                    .build();
            ServiceResponse<String> cos = service.register(admin);

            noteRepository.save(new Note(userRepository.findByName(admin.getName()).get(), "tytul1", "<h1>TYTul 1</h1> zwykly tekst", true));
            noteRepository.save(new Note(userRepository.findByName(admin.getName()).get(), "pogrubiony", "wykly tekst <b> a tu niespodzianka</b>", true));
            noteRepository.save(new Note(userRepository.findByName(admin.getName()).get(), "pochylony", "<i>tttaka sytuacja</i>fa;sldkfjj", true));
        };
    }
}
