package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.PasswordHasher;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class BCryptPasswordHasher implements PasswordHasher {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public PasswordHash hash(String password) {
        String hashedPassword = passwordEncoder.encode(password);
        return new PasswordHash(hashedPassword);
    }

    @Override
    public boolean matches(String password, PasswordHash passwordHash) {
        return passwordEncoder.matches(password, passwordHash.value());
    }

   

}
