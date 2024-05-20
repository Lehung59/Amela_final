package com.example.demo.repository;

import com.example.demo.entity.TokenPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenPasswordRepo extends JpaRepository<TokenPassword, Integer> {
    @Query("select o from TokenPassword o where o.token=:token")
    Optional<TokenPassword> findByToken(String token);
    @Query("select o from TokenPassword o where o.user.id=:id")
    List<TokenPassword> findByUserId(int id);
}
