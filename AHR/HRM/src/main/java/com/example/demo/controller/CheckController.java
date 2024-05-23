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
//        String oldPhone = userService.getUserByEmail(email).get().getPhoneNumber();
//        if(phone.equals(oldPhone)) return "OK";



        return userService.getUserByPhone(phone).isEmpty()?"OK":"TRUNG";
    }

//    @GetMapping("/admin/attendances/test")
//    public int testAttendance(@RequestParam(defaultValue = "2024-05-23") LocalDate from,
//                                           @RequestParam(defaultValue = "2024-05-03") LocalDate to){
//        LocalDate nStart = DateUtils.getFirstDayOfMonth(2024,5);
//        LocalDate nEnd = DateUtils.getLastDayOfMonth(2024,5);
//        return attendanceRepo.findAllAttendanceNoKeyword(nStart, nEnd).size();
//    }
}
