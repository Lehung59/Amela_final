package com.example.demo.controller;

import com.example.demo.form.AttendanceForm;
import com.example.demo.entity.Attendance;
import com.example.demo.repository.AttendanceRepo;
import com.example.demo.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceRepo attendanceRepo;

    @GetMapping("/admin/attendances")
    public String listAttendance(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size){

        try {
            Pageable paging = PageRequest.of(page - 1, size);
            Page<Attendance> pageTuts= attendanceRepo.findAll(paging);;
            List<Attendance> attendances= pageTuts.getContent();

            model.addAttribute("attendances", attendances);
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

//        model.addAttribute("users",userService.getAllUser());
        return "admin_attendances";
    }

    @GetMapping("/admin/attendance/insert")
    public String addAttendance(Model model ){
        Attendance attendance = new Attendance();
        model.addAttribute("attendance", attendance);
        model.addAttribute("LATE",Attendance.AttendanceStatus.LATE);
        model.addAttribute("MISS",Attendance.AttendanceStatus.MISS);
        model.addAttribute("ONTIME",Attendance.AttendanceStatus.ONTIME);
        return "admin_attendance_add";

    }
    @PostMapping("/admin/attendance/insert")
    public String saveAttendance(@Valid @ModelAttribute("attendance")Attendance attendance,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "admin_attendance_add";
        }
        attendanceService.saveAttendance(attendance);
        return "redirect:/admin/attendances";

    }
//    @GetMapping("/admin/delete/{id}")
//    public String deleteEmployee(@PathVariable int id) {
//        userService.deleteUserById(id);
//        return "redirect:/users";
//    }

//    @GetMapping("/admin/attendance/view/{id}")
//    public String viewAttendanceForm(@PathVariable int id, Model model) {
//        model.addAttribute("LATE",Attendance.AttendanceStatus.LATE);
//        model.addAttribute("MISS",Attendance.AttendanceStatus.MISS);
//        model.addAttribute("ONTIME",Attendance.AttendanceStatus.ONTIME);
//        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
//        return "admin_attendance_view";
//    }

    @GetMapping("/admin/attendance/edit/{id}")
    public String editAttendanceForm(@PathVariable int id, Model model) {
        model.addAttribute("LATE",Attendance.AttendanceStatus.LATE);
        model.addAttribute("MISS",Attendance.AttendanceStatus.MISS);
        model.addAttribute("ONTIME",Attendance.AttendanceStatus.ONTIME);
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        return "admin_attendance_edit";
    }
    @PostMapping("/admin/attendance/edit/{id}")
    public String updateAttendance(@PathVariable int id,
                             @Valid @ModelAttribute("attendance") AttendanceForm attendanceForm,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "admin_attendance_edit";
        }
        // get student from database by id
        attendanceForm.setId(id);
        attendanceService.updateAttendance(attendanceForm);

//        User exUser = userService.getUserById(id);
//        exUser.setId(id);
//        exUser.setFirstName(user.getFirstName());
//        exUser.setLastName(user.getLastName());
//        exUser.setEmail(user.getEmail());
//        exUser.setMale();
//        // save updated student object
//        userRepo.save(exUser);
        return "redirect:/admin/attendances";
    }
}
