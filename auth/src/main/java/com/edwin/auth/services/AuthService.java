package com.edwin.auth.services;

import com.edwin.auth.dto.LoginRequest;
import com.edwin.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse autenticar(LoginRequest request) throws Exception;
}
