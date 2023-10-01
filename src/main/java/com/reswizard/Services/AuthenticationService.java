package com.reswizard.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void authenticateUser(String username, String password) {
        // Создайте объект UsernamePasswordAuthenticationToken с данными пользователя
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // Вызовите метод authenticate для AuthenticationManager
        authenticationManager.authenticate(authenticationToken);

        // Если аутентификация прошла успешно, установите контекст безопасности
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
