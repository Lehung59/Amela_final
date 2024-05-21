package com.example.demo.service.implement;

import com.example.demo.constant.Constants;
import com.example.demo.entity.Token;
import com.example.demo.entity.User;
import com.example.demo.repository.TokenRepo;
import com.example.demo.service.TokenService;
import com.example.demo.service.TokenService;
import com.example.demo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private  final TokenRepo tokenRepo;


    @Override
    public Optional<Token> findByToken(String token, Token.TokenType tokenType) {
        Optional<Token> tokenPassword = tokenRepo.findByToken(token, Token.TokenType.PASSWORD);
        if (tokenPassword.isEmpty()) {
            return Optional.empty();
        }
        if(tokenPassword.get().getTokenType()!= tokenType){
            return Optional.empty();
        }
        if (tokenPassword.get().isRevoked()){
            return Optional.empty();
        }
        if (tokenPassword.get().getTokenExpried().before(DateUtils.getCurrentDay())){
            return Optional.empty();
        }
        return tokenPassword;
    }

    @Override
    public void revokeToken(Token token) {

        token.setRevoked(true);
        tokenRepo.save(token);
    }

    @Override
    public Token createToken(User user, Token.TokenType tokenType) {
        List<Token> list = tokenRepo.findByUserId(user.getId(), tokenType);
        if(!list.isEmpty()){
            list.forEach(token -> {
                token.setRevoked(true);
                tokenRepo.save(token);
            });
        }

        String token = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        Token t = Token.builder()
                .token(token)
                .tokenExpried(new Date(DateUtils.getCurrentDay().getTime() + Constants.TOKEN_EXP))
                .isRevoked(false)
                .tokenType(tokenType)
                .user(user)
                .build();
        return tokenRepo.save(t);
    }



}
