package com.tp.F1WebSite.services;

import com.tp.F1WebSite.dto.auth.LoginDto;
import com.tp.F1WebSite.dto.auth.RegisterDto;
import com.tp.F1WebSite.dto.auth.AuthResponseDto;

public interface AuthenticationService {

    AuthResponseDto loginUser(LoginDto loginDto);

    void registerUser(RegisterDto registerDto);
}
