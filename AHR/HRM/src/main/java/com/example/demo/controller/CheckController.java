package com.example.demo.controller;

import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckController {
    public final UserService userService;

    @GetMapping("/check/user/email")
    public String checkUserEmail(@RequestParam String email){
        return userService.getUserByEmail(email).isEmpty()?"OK":"TRUNG";
    }
    @GetMapping("/check/user/phone")
    public String checkUserPhone(@RequestParam String phone){
        return userService.getUserByPhone(phone).isEmpty()?"OK":"TRUNG";
    }
}
