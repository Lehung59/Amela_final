package com.example.demo.utils;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils {
    private final UserRepo userRepo;

    public String getUserName(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        String email = customUserDetails.getUsername();
        return email;
    }
    public User getUserNow(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        String email = customUserDetails.getUsername();
        return userRepo.findByEmail(email).get();
    }
}
