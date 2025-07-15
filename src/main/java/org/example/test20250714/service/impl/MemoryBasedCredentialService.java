package org.example.test20250714.service.impl;

import java.util.Set;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.test20250714.model.Credentials;
import org.example.test20250714.service.CacheService;
import org.example.test20250714.service.CredentialService;
import org.example.test20250714.service.PasswordHashService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoryBasedCredentialService implements CredentialService {

    private final CacheService cacheService;
    private final PasswordHashService hashService;

    @Override
    public void add(String login, char[] password) {
        StringBuilder passwordSequence = new StringBuilder().append(password);
        String hash = hashService.hash(passwordSequence);
        cacheService.add(login, hash);
    }

    @Override
    public boolean exist(char[] password) {
        StringBuilder passwordSequence = new StringBuilder().append(password);
        return existHash(passwordSequence) || existPassword(passwordSequence);
    }

    private boolean existHash(CharSequence passwordHash) {
        boolean exist = exist(cred -> cred.contentEquals(passwordHash));
        log.info("наличие пароля в кэше: {}", exist);
        return exist;
    }

    private boolean existPassword(CharSequence password) {
        boolean exist = exist(cred -> hashService.matches(password, cred));
        log.info("Наличие хэша пароля в кэше: {}", exist);
        return exist;
    }

    private boolean exist(Predicate<String> inCachePredicate) {
        Set<Credentials> cache = cacheService.getAll();
        return cache.stream()
                .map(Credentials::getPasswordHash)
                .anyMatch(inCachePredicate);
    }
}