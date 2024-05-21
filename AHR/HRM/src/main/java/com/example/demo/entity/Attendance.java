package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Entity
@Data
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_check")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCheck;

    @DateTimeFormat(pattern = "HH:mm:ss")
    @Column(name = "time_check_in")
    private LocalTime timeCheckIn;
    @DateTimeFormat(pattern = "HH:mm:ss")
    @Column(name = "time_check_out")
    private LocalTime timeCheckOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttendanceStatus status;

    @Column(name="allowed")
    private boolean allowed;

    @Column(name = "note")
    private String note;

    @Column(name = "work_time")
    private double workTime;


    public enum AttendanceStatus {
        LATE,  MISS, ONTIME
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attendance_user"))
    @JsonBackReference
    private User user;
}
