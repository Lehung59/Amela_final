package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.form.AttendanceForm;
import com.example.demo.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<Attendance> getAllAttendance();
    Attendance getAttendanceById(int id) throws Exception;
    void saveAttendance(AttendanceForm attendance) throws Exception;
    void updateAttendance(AttendanceForm attendanceForm);
    void deleteAttendance(int id) throws Exception;

    AttendanceForm setCheckIn(int id);

    Page<Attendance> getAllAttendancePaginable(SearchForm searchForm);

    List<Attendance> findByEmailAndDate(String email, LocalDate dateCheck);

    Page<AttendanceForm> getAllAttendanceByIdPaginable(int page, int size, int id, LocalDate startDate, LocalDate endDate);



}
