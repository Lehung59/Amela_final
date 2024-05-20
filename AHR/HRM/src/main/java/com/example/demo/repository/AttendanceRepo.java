package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo extends JpaRepository<Attendance, Integer> {
    @Query("select o from Attendance o where o.dateCheck=:checkDate and o.user.email=:email ")
    List<Attendance> findByEmailAndDate(String email, LocalDate checkDate);


    @Query("select o from Attendance  o where o.user.email LIKE %?1%"
            + "or CONCAT(o.user.firstName, ' ', o.user.lastName) LIKE %?1%")
    Page<Attendance> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    @Query("select o from Attendance o where o.dateCheck =:date and o.user.id =:id")
    Optional<Attendance> findByDateAndUserId(LocalDate date, int id);
//    @Query("select o from Attendance o where o.user.email=:email")
//    Optional<Attendance> findByEmail(String email);
}
