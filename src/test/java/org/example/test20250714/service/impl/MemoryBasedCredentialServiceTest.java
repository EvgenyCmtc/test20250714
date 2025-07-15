package org.example.test20250714.service.impl;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.test20250714.model.Credentials;
import org.example.test20250714.service.CacheService;
import org.example.test20250714.service.PasswordHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemoryBasedCredentialServiceTest {

    private static final String USER1 = "user1";
    private static final String USER2 = "user2";

    private static final String PASSWORD11 = "password11";
    private static final String PASSWORD12 = "password12";

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    private CacheService cacheService = new MemoryBasedCacheService(objectMapper);
    @Spy
    private PasswordHashService hashService = new BCryptPasswordHashService(passwordEncoder);

    @InjectMocks
    private MemoryBasedCredentialService credentialService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addOnceByOneUser() {
        credentialService.add(USER1, PASSWORD11.toCharArray());

        Set<String> cachedHashes = getCachedHashes();

        assertThat(cachedHashes.size()).isEqualTo(1);
        assertThat(existPassword(PASSWORD11, cachedHashes)).isEqualTo(true);
    }

    @Test
    void addRepeatByOneUser() {
        credentialService.add(USER1, PASSWORD11.toCharArray());
        credentialService.add(USER1, PASSWORD12.toCharArray());

        Set<String> cachedHashes = getCachedHashes();

        assertThat(cachedHashes.size()).isEqualTo(1);
        assertThat(existPassword(PASSWORD11, cachedHashes)).isEqualTo(false);
        assertThat(existPassword(PASSWORD12, cachedHashes)).isEqualTo(true);
    }

    @Test
    void addOnceByTwoUsers() {
        credentialService.add(USER1, PASSWORD11.toCharArray());
        credentialService.add(USER2, PASSWORD12.toCharArray());

        Set<String> cachedHashes = getCachedHashes();

        assertThat(cachedHashes.size()).isEqualTo(2);
        assertThat(existPassword(PASSWORD11, cachedHashes)).isEqualTo(true);
        assertThat(existPassword(PASSWORD12, cachedHashes)).isEqualTo(true);
    }

    @Test
    void existByPassword() {
        credentialService.add(USER1, PASSWORD11.toCharArray());

        assertThat(credentialService.exist(PASSWORD11.toCharArray())).isEqualTo(true);
        assertThat(credentialService.exist(PASSWORD12.toCharArray())).isEqualTo(false);
    }

    @Test
    void existByHash() {
        credentialService.add(USER1, PASSWORD11.toCharArray());

        Set<String> cachedHashes = getCachedHashes();

        assertThat(credentialService.exist(cachedHashes.iterator().next().toCharArray())).isEqualTo(true);
    }

    private Set<String> getCachedHashes() {
        return cacheService.getAll().stream()
                .filter(cred -> USER1.equals(cred.getLogin()) || USER2.equals(cred.getLogin()))
                .map(Credentials::getPasswordHash)
                .collect(Collectors.toSet());
    }

    private boolean existPassword(String password, Set<String> cachedHashes) {
        boolean exist = false;
        for (String cachedHash : cachedHashes) {
          exist = exist || hashService.matches(password, cachedHash);
        }
        return exist;
    }
}