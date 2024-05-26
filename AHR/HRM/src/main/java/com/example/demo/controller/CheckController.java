package com.example.demo.controller;

import com.example.demo.entity.Attendance;
import com.example.demo.repository.AttendanceRepo;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckController {
    public final UserService userService;
    private final AttendanceRepo attendanceRepo;

    @GetMapping("/check/user/email")
    public String checkUserEmail(@RequestParam String email){
        return userService.getUserByEmail(email).isEmpty()?"OK":"TRUNG";
    }
    @GetMapping("/check/user/phone")
    public String checkUserPhone(@RequestParam String phone){
        return userService.getUserByPhone(phone).isEmpty()?"OK":"TRUNG";
    }


}
