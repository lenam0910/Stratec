package com.example.satrect.service;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.example.satrect.dto.request.LoginRequest;
import com.example.satrect.dto.request.UsersRequest;
import com.example.satrect.dto.response.LoginResponse;
import com.example.satrect.dto.response.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    UserResponse logout(User user);
}
