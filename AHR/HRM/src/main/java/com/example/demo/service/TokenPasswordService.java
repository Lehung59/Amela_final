package com.example.demo.service;

import com.example.demo.entity.TokenPassword;
import com.example.demo.entity.User;

import java.util.Optional;

public interface TokenPasswordService {
    Optional<TokenPassword> findByToken(String token);

    void revokeToken(TokenPassword tokenPassword);

    TokenPassword createToken(User user);
}


