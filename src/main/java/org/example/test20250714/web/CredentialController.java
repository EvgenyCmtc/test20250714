package org.example.test20250714.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.test20250714.web.dto.CredentialsDto;
import org.example.test20250714.service.CredentialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService service;

    @PostMapping(value = "/credentials")
    public void addCredentials(@RequestBody CredentialsDto credentials) {
        log.info("Добавление в кеш login = {}", credentials.getLogin());
        service.add(credentials.getLogin(), credentials.getPassword());
    }


    @GetMapping(value = "/credentials/exist")
    public boolean addCredentials(@RequestParam(value = "password") char[] password) {
        log.info("Проверка в кеше пароля или хэша");
        return service.exist(password);
    }
}
