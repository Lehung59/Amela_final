package com.example.demo.form;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttendanceForm {

    private int id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCheck;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime timeCheckIn;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime timeCheckOut;

    private double workTime;

    private AttendanceStatus status;

    private boolean allowed;

    private String note;

    public enum AttendanceStatus {
        LATE,  MISS, ONTIME
    }

    private User user;
}
