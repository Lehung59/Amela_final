package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.form.AttendanceForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface AttendanceService {
    List<Attendance> getAllAttendance();
    Attendance getAttendanceById(int id);
    void saveAttendance(Attendance attendance);
    void updateAttendance(AttendanceForm attendanceForm);
    void deleteAttendance(int id);
//    List<Attendance> searchAttendance(String keyword);
//    Page<Attendance> searchAttendance(String keyword, int pageNo, int pageSize);

//     Page<Attendance> getAllUser(int pageNumber, String keyword) ;


}
