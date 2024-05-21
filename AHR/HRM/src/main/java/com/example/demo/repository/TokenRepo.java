package com.example.demo.repository;

import com.example.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {
    @Query("select o from Token o where o.token=:token and o.tokenType=:tokenType ")
    Optional<Token> findByToken(String token, Token.TokenType tokenType);
    @Query("select o from Token o where o.user.id=:id and o.tokenType=:tokenType")
    List<Token> findByUserId(int id, Token.TokenType tokenType);
}
