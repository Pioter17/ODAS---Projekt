package com.example.demo.models;


import com.example.demo.other.Role;
import com.example.demo.other.SecretEncryption;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;
    @Getter
    private String name;
    private String password;

    @Getter(AccessLevel.NONE)
    @Column(name = "secret", length = 4096)
    private String secret;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<Note> notes = new ArrayList<>();

    public User(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        try {
            this.secret = SecretEncryption.encrypt(SecretEncryption.buildSecret(name,password));
        } catch (Exception e){
            this.secret = SecretEncryption.buildSecret(name,password);
        }
        this.role = role;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd(String passwd) {
        this.password = passwd;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getSecret() {
        try{
            return  SecretEncryption.decrypt(this.secret);
        }catch (Exception e) {
            return this.secret;
        }
    }

}
