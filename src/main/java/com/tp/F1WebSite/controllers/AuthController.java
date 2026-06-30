package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.dto.auth.LoginDto;
import com.tp.F1WebSite.dto.auth.RegisterDto;
import com.tp.F1WebSite.dto.auth.AuthResponseDto;
import com.tp.F1WebSite.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        authenticationService.registerUser(registerDto);

        return new ResponseEntity<>("Register successful", HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        AuthResponseDto authResponseDto = authenticationService.loginUser(loginDto);

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }
}