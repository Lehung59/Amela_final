package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query(" select o from Attendance o where o.user.id=:id and o.dateCheck BETWEEN :startDate AND :endDate ")
    Page<Attendance> findAllByUserId(Pageable paging, int id, LocalDate startDate, LocalDate endDate);


    @Query("SELECT o FROM Attendance o " +
            "WHERE o.user.email LIKE %:keyword% " +
            "OR CONCAT(o.user.firstName, ' ', o.user.lastName) LIKE %:keyword%")
    Page<Attendance> findAllAttendance(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT o FROM Attendance o " +
            "WHERE (o.user.email LIKE %:keyword% " +
            "OR CONCAT(o.user.firstName, ' ', o.user.lastName) LIKE %:keyword%) " +
            "AND o.dateCheck BETWEEN :startDate AND :endDate")
    Page<Attendance> findAllAttendance(Pageable pageable,
                                       @Param("keyword") String keyword,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Attendance o " +
            "WHERE o.dateCheck BETWEEN :startDate AND :endDate")
    Page<Attendance> findAllAttendanceNoKeyword(Pageable pageable,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Attendance o")
    Page<Attendance> findAllAttendanceNoKeyword(Pageable pageable);
    @Query("SELECT o FROM Attendance o " +
            "where o.dateCheck BETWEEN :startDate AND :endDate")
    List<Attendance> findAllAttendanceNoKeyword(
                                   LocalDate startDate,
                                    LocalDate endDate);

}
