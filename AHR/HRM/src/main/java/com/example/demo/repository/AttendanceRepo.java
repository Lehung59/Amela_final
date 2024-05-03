package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface AttendanceRepo extends JpaRepository<Attendance, Integer> {
    @Query("select o from Attendance o where o.dateCheck=:checkDate and o.user.email=:email ")
    Optional<Attendance> findByEmailAndDate(String email, LocalDate checkDate);
//    @Query("select o from Attendance o where o.user.email=:email")
//    Optional<Attendance> findByEmail(String email);
}
