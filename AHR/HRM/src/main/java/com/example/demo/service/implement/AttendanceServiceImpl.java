package com.example.demo.service.implement;

import com.example.demo.exception.AttendanceExistException;
import com.example.demo.exception.AttendanceNotFoundException;
import com.example.demo.exception.MailNotFoundException;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import com.example.demo.form.AttendanceForm;
import com.example.demo.repository.AttendanceRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.AttendanceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, UserRepo userRepo){
        this.attendanceRepo = attendanceRepo;
        this.userRepo = userRepo;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepo.findAll();
    }

    @Override
    public Attendance getAttendanceById(int id) {
        Optional<Attendance> attendance = attendanceRepo.findById(id);
        if(attendance.isEmpty()) {
            throw new AttendanceNotFoundException("Attendance not found with ID: " + id);

        }
        return attendance.get();
    }

    @Override
    public void saveAttendance(Attendance attendance)
    {
        LocalDate checkDate = attendance.getDateCheck();
        String email = attendance.getUser().getEmail();
//        if(userRepo.findByEmail(email).isEmpty()){
//            throw new MailNotFoundException("Not found email: " + email);
//        }
//        if(attendanceRepo.findByEmailAndDate(email,checkDate).isPresent() ){
//            throw new AttendanceExistException("Exist this attendance. Please edit it");
//        }
        User user = userRepo.findByEmail(email).get();


        Attendance newObj = new Attendance();
        if (attendance.isAllowed()) {
            newObj.setAllowed(true);
            newObj.setNote(attendance.getNote());
        } else {
            newObj.setAllowed(false);
            newObj.setNote(null);
        }
        newObj.setDateCheck(checkDate);
        newObj.setUser(user);
        newObj.setNote(attendance.getNote());
        if(attendance.getStatus().equals(Attendance.AttendanceStatus.MISS) ){
            newObj.setStatus(Attendance.AttendanceStatus.MISS);
        } else if(attendance.getStatus().equals(Attendance.AttendanceStatus.LATE) ){
            newObj.setStatus(Attendance.AttendanceStatus.LATE);
        } else if(attendance.getTimeCheckIn().isAfter(LocalTime.parse("08:00:00"))){
            newObj.setStatus(Attendance.AttendanceStatus.LATE);
        } else {
            newObj.setStatus(Attendance.AttendanceStatus.ONTIME);
        }
        attendanceRepo.save(newObj);
    }

    @Override
    public void updateAttendance(AttendanceForm attendanceForm) {
        Attendance attendance = mapFormToEntity(attendanceForm);
        Attendance oldObj = attendanceRepo.findById(attendance.getId()).get();
        oldObj.setDateCheck(attendance.getDateCheck());
        if(attendance.getTimeCheckOut() != null) oldObj.setTimeCheckOut(attendance.getTimeCheckOut());
        if(attendance.getTimeCheckIn() != null) oldObj.setTimeCheckIn(attendance.getTimeCheckIn());
        if(attendance.getNote() != null) oldObj.setNote(attendance.getNote());

        if(attendance.getTimeCheckIn().isBefore(LocalTime.parse("08:00:00"))) {
            oldObj.setStatus(Attendance.AttendanceStatus.ONTIME);
        }  else if(attendance.getTimeCheckIn().isAfter(LocalTime.parse("08:00:00"))) {
            oldObj.setStatus(Attendance.AttendanceStatus.LATE);
        }else if(attendance.getStatus().equals(Attendance.AttendanceStatus.MISS) ){
            oldObj.setStatus(Attendance.AttendanceStatus.MISS);
        } else if(attendance.getStatus().equals(Attendance.AttendanceStatus.LATE) ){
            oldObj.setStatus(Attendance.AttendanceStatus.LATE);
        }
//        if(attendance.getStatus() != null) oldObj.setStatus(attendance.getStatus());
//        oldObj.setAllowed(attendance.isAllowed());

        if (attendance.isAllowed()) {
            oldObj.setAllowed(true);
            oldObj.setNote(attendance.getNote());
        } else {
            oldObj.setAllowed(false);
            oldObj.setNote(null);
        }

        attendanceRepo.save(oldObj);
    }

    @Override
    public void deleteAttendance(int id) {
        Optional<Attendance> attendance = attendanceRepo.findById(id);
        if(attendance.isEmpty()) {
            throw new AttendanceNotFoundException("Attendance not found with ID: " + id);

        }
        attendanceRepo.deleteById(id);
    }
    public Attendance mapFormToEntity(AttendanceForm form) {
        Attendance entity = modelMapper.map(form, Attendance.class);
        return entity;
    }
}
