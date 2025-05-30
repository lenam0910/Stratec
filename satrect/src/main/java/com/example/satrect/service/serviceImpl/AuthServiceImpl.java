package com.example.satrect.service.serviceImpl;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.satrect.dto.request.LoginRequest;
import com.example.satrect.dto.request.UsersRequest;
import com.example.satrect.dto.response.LoginResponse;
import com.example.satrect.dto.response.UserResponse;
import com.example.satrect.entity.Users;
import com.example.satrect.repository.UserRepository;
import com.example.satrect.service.AuthService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    JwtServiceImpl jwtService;
    AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        veryfiUsernamePassword(loginRequest);
        Users user = userRepository.findByUserName(loginRequest.getUserName());
        var token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .isValid(true)
                .build();
    }

    @Override
    public UserResponse logout(User user) {

        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }

    public void veryfiUsernamePassword(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new UnsupportedOperationException("unauthenticated");
        }
    }

}
