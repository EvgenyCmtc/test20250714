package org.example.test20250714.service;

import java.util.Set;
import org.example.test20250714.model.Credentials;

public interface CacheService {

    void add(String login, String passwordHash);

    Set<Credentials> getAll();
}
