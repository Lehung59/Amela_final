package com.example.demo.service;

import com.example.demo.entity.Token;
import com.example.demo.entity.User;

import java.util.Optional;

public interface TokenService {
    Optional<Token> findByToken(String token, Token.TokenType tokenType);

    void revokeToken(Token token);

    Token createToken(User user, Token.TokenType tokenType);
}


