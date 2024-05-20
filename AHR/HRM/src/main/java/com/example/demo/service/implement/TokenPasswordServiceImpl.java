package com.example.demo.service.implement;

import com.example.demo.entity.TokenPassword;
import com.example.demo.entity.User;
import com.example.demo.repository.TokenPasswordRepo;
import com.example.demo.service.TokenPasswordService;
import com.example.demo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenPasswordServiceImpl implements TokenPasswordService {
    private  final TokenPasswordRepo tokenPasswordRepo;


    @Override
    public Optional<TokenPassword> findByToken(String token) {
        Optional<TokenPassword> tokenPassword = tokenPasswordRepo.findByToken(token);
        if (tokenPassword.isEmpty()) {
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
    public void revokeToken(TokenPassword tokenPassword) {

        tokenPassword.setRevoked(true);
        tokenPasswordRepo.save(tokenPassword);
    }

    @Override
    public TokenPassword createToken(User user) {
        List<TokenPassword> list = tokenPasswordRepo.findByUserId(user.getId());
        if(!list.isEmpty()){
            list.forEach(tokenPassword -> {
                tokenPassword.setRevoked(true);
                tokenPasswordRepo.save(tokenPassword);
            });        }

        String token = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        TokenPassword tokenPassword = TokenPassword.builder()
                .token(token)
                .tokenExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000))
                .isRevoked(false)
                .user(user)
                .build();
        return tokenPasswordRepo.save(tokenPassword);
    }
}
