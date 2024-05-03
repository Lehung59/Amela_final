package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.form.AttendanceForm;

import java.util.List;

public interface AttendanceService {
    List<Attendance> getAllAttendance();
    Attendance getAttendanceById(int id);
    void saveAttendance(Attendance attendance);
    Attendance updateAttendance(AttendanceForm attendanceForm);
    void deleteAttendance(int id);

}
