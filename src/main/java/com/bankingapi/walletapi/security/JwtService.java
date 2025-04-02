package com.bankingapi.walletapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    public boolean isTokenValid(String token, String username) {
        return jwtUtil.isTokenValid(token, username);

    }
}
