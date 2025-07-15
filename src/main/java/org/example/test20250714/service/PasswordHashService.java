package org.example.test20250714.service;

public interface PasswordHashService {

    String hash(CharSequence password);

    boolean matches(CharSequence password, String hash);
}
