package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.entities.UserEntity;
import com.tp.F1WebSite.domain.enums.UserRole;
import com.tp.F1WebSite.dto.auth.LoginDto;
import com.tp.F1WebSite.dto.auth.RegisterDto;
import com.tp.F1WebSite.repositories.UserRepository;
import com.tp.F1WebSite.security.JwtGenerator;
import com.tp.F1WebSite.dto.auth.AuthResponseDto;
import com.tp.F1WebSite.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtGenerator jwtGenerator;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public void registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already exists"
            );
        }

        UserEntity user = UserEntity.builder()
                .firstname(registerDto.getFirstname())
                .lastname(registerDto.getLastname())
                .email(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponseDto loginUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword())
            );
            String token = jwtGenerator.generateToken(authentication);
            return new AuthResponseDto(token);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Wrong login or password"
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server is dead"
            );
        }
    }
}
