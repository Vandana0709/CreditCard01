package com.bank.portal.web;

import com.bank.portal.service.BankingService;
import com.bank.portal.web.ApiDtos.LoginView;
import com.bank.portal.web.ApiDtos.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final BankingService service;

    @GetMapping("/login")
    public LoginView login(Authentication authentication) {
        return service.login(authentication.getName());
    }

    @PostMapping("/signup")
    public LoginView signup(@Valid @RequestBody SignupRequest request) {
        return service.signup(request);
    }
}