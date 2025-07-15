package org.example.test20250714.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.test20250714.exception.CredentialException;
import org.example.test20250714.model.Credentials;
import org.example.test20250714.service.CacheService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoryBasedCacheService implements CacheService {

    private final Set<String> storage = ConcurrentHashMap.newKeySet();

    private final ObjectMapper objectMapper;

    @Override
    public void add(String login, String passwordHash) {
        storage.stream()
//                .map(this::fromJson)
                .filter(credJson -> login.equals(fromJson(credJson).getLogin()))
                .findAny()
                .ifPresentOrElse(
                        credJson -> {
                            Credentials cred = fromJson(credJson);
                            cred.setPasswordHash(passwordHash);
                            storage.remove(credJson);
                            storage.add(toJson(cred));
                        },
                        () -> storage.add(toJson(new Credentials(login, passwordHash)))
                );
    }

    @Override
    public Set<Credentials> getAll() {
        return storage.stream()
                .map(this::fromJson)
                .collect(Collectors.toSet());
    }


    private String toJson(Credentials credentials) {
        try {
            return objectMapper.writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            throw new CredentialException(e);
        }
    }

    private Credentials fromJson(String credentials) {
        try {
            return objectMapper.readValue(credentials, Credentials.class);
        } catch (JsonProcessingException e) {
            throw new CredentialException(e);
        }
    }
}
