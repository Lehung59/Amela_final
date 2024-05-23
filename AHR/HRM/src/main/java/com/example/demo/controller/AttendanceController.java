package com.example.demo.controller;

import com.example.demo.constant.Constants;
import com.example.demo.entity.MailStatus;
import com.example.demo.exception.AttendanceExistException;
import com.example.demo.form.AttendanceForm;
import com.example.demo.entity.Attendance;
import com.example.demo.form.MailForm;
import com.example.demo.form.SearchForm;
import com.example.demo.repository.AttendanceRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final UserUtils userUtils;
    private final DateUtils dateUtils;


    @GetMapping("/admin/attendances")
    public String listAttendance(Model model,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(defaultValue = "2024") Integer year,
                                 @RequestParam(required = false) LocalDate fromDate,
                                 @RequestParam(required = false) LocalDate toDate,
                                 @RequestParam(defaultValue = Constants.PAGE) int page,
                                 @RequestParam(defaultValue = Constants.SIZE) int size) {

        try {
            if (month == null) month = DateUtils.getCurrentMonth();
            SearchForm searchForm = SearchForm.builder()
                    .keyword(keyword)
                    .year(year)
                    .month(month)
                    .page(page)
                    .size(size)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .build();
            Page<Attendance> pageTuts = attendanceService.getAllAttendancePaginable(searchForm);

            model.addAttribute("searchForm", searchForm);
            if (keyword != null)
                model.addAttribute("keyword", keyword);
            model.addAttribute("attendances", pageTuts.getContent());
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
    public String addAttendance(Model model) {
        Attendance attendance = new Attendance();
        model.addAttribute("attendance", attendance);
        model.addAttribute("LATE", Attendance.AttendanceStatus.LATE);
        model.addAttribute("MISS", Attendance.AttendanceStatus.MISS);
        model.addAttribute("ONTIME", Attendance.AttendanceStatus.ONTIME);
        return "admin_attendance_add";

    }

    @PostMapping("/admin/attendance/insert")
    public String saveAttendance(@Valid @ModelAttribute("attendance") Attendance attendance,
                                 BindingResult bindingResult) {


        List<Attendance> newObj = attendanceService.findByEmailAndDate(attendance.getUser().getEmail(), attendance.getDateCheck());
        if (!newObj.isEmpty()) {
            bindingResult.rejectValue("dateCheck", "Đã tồn tại chấm công này");
        }

        if (bindingResult.hasErrors()) {

            return "admin_attendance_add";
        }

        attendanceService.saveAttendance(attendance);
        return "redirect:/admin/attendances";

    }

    @GetMapping("/admin/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable int id) {
        attendanceService.deleteAttendance(id);
        return "redirect:/admin/attendances";
    }

    @GetMapping("/admin/attendance/view/{id}")
    public String viewAttendanceForm(@PathVariable int id, Model model) {
        model.addAttribute("LATE", Attendance.AttendanceStatus.LATE);
        model.addAttribute("MISS", Attendance.AttendanceStatus.MISS);
        model.addAttribute("ONTIME", Attendance.AttendanceStatus.ONTIME);
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        return "admin_attendance_view";
    }

    @GetMapping("/admin/attendance/edit/{id}")
    public String editAttendanceForm(@PathVariable int id, Model model) {
        model.addAttribute("LATE", Attendance.AttendanceStatus.LATE);
        model.addAttribute("MISS", Attendance.AttendanceStatus.MISS);
        model.addAttribute("ONTIME", Attendance.AttendanceStatus.ONTIME);
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        return "admin_attendance_edit";
    }

    @PostMapping("/admin/attendance/edit/{id}")
    public String updateAttendance(@PathVariable int id,
                                   @Valid @ModelAttribute("attendance") AttendanceForm attendanceForm,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin_attendance_edit";
        }

        attendanceForm.setId(id);
        attendanceService.updateAttendance(attendanceForm);
        return "redirect:/admin/attendances";
    }

    @GetMapping("/user/attendanceCheck")
    public String attendanceCheck(Model model,
                                  @RequestParam(required = false) Integer month,
                                  @RequestParam(defaultValue = "2024") Integer year,
                                  @RequestParam(defaultValue = Constants.PAGE) int page,
                                  @RequestParam(defaultValue = Constants.SIZE) int size,
                                  @RequestParam(required = false) LocalDate fromDate,
                                  @RequestParam(required = false) LocalDate toDate) {
        try {
            LocalDate startDate = DateUtils.getFirstDayOfMonth(year,month);
            LocalDate endDate = DateUtils.getLastDayOfMonth(year,month);

            String email = userUtils.getUserName();
            int id = userService.getUserByEmail(email).get().getId();
            Page<AttendanceForm> pageTuts = attendanceService.getAllAttendanceByIdPaginable(page, size, id,startDate,endDate);
            List<AttendanceForm> attendanceForms = pageTuts.getContent();
            if (month == null) month = DateUtils.getCurrentMonth();
            SearchForm searchForm = SearchForm.builder()
                    .keyword(null)
                    .page(page)
                    .size(size)
                    .year(year)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .month(month)
                    .build();

            model.addAttribute("searchForm", searchForm);
            model.addAttribute("attendances", pageTuts.getContent());
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);


            AttendanceForm attendanceNow = attendanceService.setCheckIn(id);
            model.addAttribute("id", id);
            model.addAttribute("attendanceNow", attendanceNow);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "user_attendance_check";
    }

    @GetMapping("/user/checkout")
    public String attendanceCheckout(RedirectAttributes redirectAttributes){
        String email = userUtils.getUserName();
        int id = userService.getUserByEmail(email).get().getId();
        attendanceService.setCheckIn(id);
        redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Check out thành công");
        return "redirect:/user/attendanceCheck";

    }

}
