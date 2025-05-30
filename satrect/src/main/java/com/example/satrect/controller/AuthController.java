package com.example.satrect.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.satrect.dto.request.LoginRequest;
import com.example.satrect.dto.request.UsersRequest;
import com.example.satrect.dto.response.ApiResponse;
import com.example.satrect.dto.response.LoginResponse;
import com.example.satrect.dto.response.UserResponse;
import com.example.satrect.service.AuthService;
import com.example.satrect.utils.Notification;
import com.nimbusds.jose.KeyLengthException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    @PostMapping("register")
    public ApiResponse<Object> postMethodName(@RequestBody UsersRequest usersRequest) {
        UserResponse userResponse = authService.register(usersRequest);

        return ApiResponse.builder()
                .code(1000)
                .message(Notification.REGISTER_SUCCESS)
                .data(userResponse)
                .build();
    }

    @PostMapping("/login-cookie")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response)
            throws KeyLengthException {
        LoginResponse loginRespone = authService.login(loginRequest);
        String token = loginRespone.getToken();
        // ✅ Set token vào HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 10) // 10h
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Đăng nhập thành công");
    }
}
