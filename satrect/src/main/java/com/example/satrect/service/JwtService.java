package com.example.satrect.service;

import com.example.satrect.entity.Users;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateToken(Users user);

    Boolean verify(String token) throws JOSEException, ParseException;

    String getSubject(String token);
}
