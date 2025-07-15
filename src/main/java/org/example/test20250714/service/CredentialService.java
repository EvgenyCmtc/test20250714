package org.example.test20250714.service;

public interface CredentialService {

    void add(String login, char[] password);

    boolean exist(char[] password);
}
