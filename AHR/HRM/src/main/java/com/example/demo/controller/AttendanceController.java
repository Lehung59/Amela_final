package com.example.demo.controller;

import com.example.demo.constant.Constants;
import com.example.demo.form.AttendanceForm;
import com.example.demo.entity.Attendance;
import com.example.demo.form.SearchForm;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                                 @RequestParam(defaultValue = Constants.SIZE) int size,
                                 RedirectAttributes redirectAttributes) {

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
            return "admin_attendances";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/attendances";
        }

//        model.addAttribute("users",userService.getAllUser());
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
    public String saveAttendance(@Valid @ModelAttribute("attendance") AttendanceForm attendance,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        try{
            if (bindingResult.hasErrors()) {
                Map<String, String> errors= new HashMap<>();
                bindingResult.getFieldErrors().forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );
                redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());
                return "redirect:/admin/attendance/insert";
            }
            List<Attendance> newObj = attendanceService.findByEmailAndDate(attendance.getUser().getEmail(), attendance.getDateCheck());
            if (!newObj.isEmpty()) {
                throw new Exception("This timekeeping already exists");
            }

            attendanceService.saveAttendance(attendance);
            return "redirect:/admin/attendances";

        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());

            return "redirect:/admin/attendance/insert";
        }


    }

    @GetMapping("/admin/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable int id,
                                   RedirectAttributes redirectAttributes) {
        try{
            attendanceService.deleteAttendance(id);
            return "redirect:/admin/attendances";
        } catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/attendances";
        }

    }

    @GetMapping("/admin/attendance/view/{id}")
    public String viewAttendanceForm(@PathVariable int id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try{
            model.addAttribute("LATE", Attendance.AttendanceStatus.LATE);
            model.addAttribute("MISS", Attendance.AttendanceStatus.MISS);
            model.addAttribute("ONTIME", Attendance.AttendanceStatus.ONTIME);
            model.addAttribute("attendance", attendanceService.getAttendanceById(id));
            return "admin_attendance_view";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/attendances";
        }

    }

    @GetMapping("/admin/attendance/edit/{id}")
    public String editAttendanceForm(@PathVariable int id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("LATE", Attendance.AttendanceStatus.LATE);
            model.addAttribute("MISS", Attendance.AttendanceStatus.MISS);
            model.addAttribute("ONTIME", Attendance.AttendanceStatus.ONTIME);
            model.addAttribute("attendance", attendanceService.getAttendanceById(id));
            return "admin_attendance_edit";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/attendances";
        }

    }

    @PostMapping("/admin/attendance/edit/{id}")
    public String updateAttendance(@PathVariable int id,
                                   @Valid @ModelAttribute("attendance") AttendanceForm attendanceForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        try{

            if (bindingResult.hasErrors()) {
                Map<String, String> errors= new HashMap<>();

                bindingResult.getFieldErrors().forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );
                redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());
                return "redirect:/admin/attendance/edit/"+id;
            }

            attendanceForm.setId(id);
            attendanceService.updateAttendance(attendanceForm);
            return "redirect:/admin/attendances";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/attendance/edit/"+id;
        }

    }

    @GetMapping("/user/attendanceCheck")
    public String attendanceCheck(Model model,
                                  @RequestParam(required = false) Integer month,
                                  @RequestParam(defaultValue = "2024") Integer year,
                                  @RequestParam(defaultValue = Constants.PAGE) int page,
                                  @RequestParam(defaultValue = Constants.SIZE) int size,
                                  @RequestParam(required = false) LocalDate fromDate,
                                  @RequestParam(required = false) LocalDate toDate,
                                  RedirectAttributes redirectAttributes) {
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
            return "user_attendance_check";
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(Constants.ERROR,e.getMessage());
            return "redirect:/user/attendanceCheck";
        }

    }

    @GetMapping("/user/checkout")
    public String attendanceCheckout(RedirectAttributes redirectAttributes){
        try{
            String email = userUtils.getUserName();
            int id = userService.getUserByEmail(email).get().getId();
            attendanceService.setCheckIn(id);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Check out successfully");
            return "redirect:/user/attendanceCheck";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(e.getMessage());
            return "redirect:/user/attendanceCheck";
        }


    }

}
