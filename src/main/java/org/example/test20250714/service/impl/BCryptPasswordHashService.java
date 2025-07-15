package org.example.test20250714.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.test20250714.service.PasswordHashService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BCryptPasswordHashService implements PasswordHashService {

    private final PasswordEncoder bcryptPasswordEncoder;

    @Override
    public String hash(CharSequence src) {
        return bcryptPasswordEncoder.encode(src);
    }

    @Override
    public boolean matches(CharSequence src, String hash) {
        return bcryptPasswordEncoder.matches(src, hash);
    }
}
