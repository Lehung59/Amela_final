package com.example.demo.controller;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import com.example.demo.form.ChangePasswordForm;
import com.example.demo.form.LoginForm;
import com.example.demo.form.UserForm;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.service.implement.UserServiceImpl.convertToUserForm;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepo userRepo;


    @GetMapping("/users")
    public String listUser(Model model,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "3") int size
                          ){

        try {
            List<User> users = new ArrayList<User>();
            Pageable paging = PageRequest.of(page - 1, size);
            Page<User> pageTuts= userRepo.findAll(paging);;
            if (keyword == null) {
                pageTuts = userRepo.findAll(paging);
            } else {
                pageTuts = userRepo.findByTitleContainingIgnoreCase(keyword, paging);
                model.addAttribute("keyword", keyword);
            }
            users = pageTuts.getContent();
            model.addAttribute("users", users);
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

//        model.addAttribute("users",userService.getAllUser());
        return "admin_users";
    }
    @GetMapping("/admin/insert")
    public String addEmployee(Model model ){

        User user = new User();
        model.addAttribute("user",user);
        return "admin_user_add";
    }
    @PostMapping("/admin/insert")
    public String saveEmployee(@Valid @ModelAttribute("user") UserForm userForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "redirect:/admin/insert?error";
        }

        userService.save(userForm);
        redirectAttributes.addFlashAttribute("mess","Da them thanh cong");
        return "redirect:/users";

    }
    @GetMapping("/user/active")
    public String activeUser(@RequestParam("email") String encodedEmail,
                             @RequestParam("activeCode") String encodedActiveCode,
                             RedirectAttributes redirectAttributes) {
        try {
            // Giải mã email và active code
            String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString());
            String activeCode = URLDecoder.decode(encodedActiveCode, StandardCharsets.UTF_8.toString());
            String newActiveCode = UUID.randomUUID().toString().substring(0,10).toUpperCase();
            // Tìm user bằng email đã giải mã
            Optional<User> user = userRepo.findByEmail(email);
            if (user.isPresent()) {
                User newUser = user.get();
                if (newUser.getCodeExpried().before(DateUtils.getCurrentDay())) {
                    redirectAttributes.addFlashAttribute("mess", "Mã kích hoạt đã hết hạn");
                    return "redirect:/login?errors=time";
                }
                if (!newUser.getActiveCode().equals(activeCode)) {
                    redirectAttributes.addFlashAttribute("mess", "Mã kích hoạt không chính xác");
                    return "redirect:/login?errors=code";
                }
                if (newUser.getIsActived()) {
                    redirectAttributes.addFlashAttribute("mess", "Tài khoản của bạn đã được kích hoạt trước đó");
                    return "redirect:/login?verified";
                }
                if (newUser.getCodeExpried().after(DateUtils.getCurrentDay()) && newUser.getActiveCode().equals(activeCode)) {
                    newUser.setIsActived(true);
                    newUser.setActiveCode(newActiveCode);
                    userRepo.save(newUser);
                    redirectAttributes.addFlashAttribute("mess", "Kích hoạt thành công");
                    return "redirect:/login?valid";
                }
            }
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here, such as logging the error or sending an error message back to the client
            redirectAttributes.addFlashAttribute("error", "An error occurred during processing your request");
            return "redirect:/login?errors=server";
        }
    }

    @GetMapping("admin/sendcode/{id}")
    public String reSendCode(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try{
            userService.reSendCode(id);
            redirectAttributes.addFlashAttribute("mess","Gửi mã thành công");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/users";
    }



    @GetMapping("/admin/delete/{id}")
    public String deleteEmployee(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try{
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("mess","Đã xóa thành công");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());

        }
        return "redirect:/users";
    }
    @GetMapping("/user/view/{id}")
    public String viewEmployee(@PathVariable int id, Model model){
        model.addAttribute("user", userService.getUserById(id));
//        model.addAttribute("role","admin");
        return "admin_user_view";
    }


    @GetMapping("/user/info/view")
    public String employeeViewProfile(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        String email = customUserDetails.getUsername();
        model.addAttribute("userForm", userService.getUserFormByEmail(email));
        model.addAttribute("id",userRepo.findByEmail(email).get().getId());
        return "user_info_view";

    }
    @GetMapping("/user/info/edit")
    public String employeeEditProfile(Model model,
                                      @ModelAttribute("error") String error){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        String email = customUserDetails.getUsername();
        model.addAttribute("userForm", userService.getUserFormByEmail(email));
        model.addAttribute("id",userRepo.findByEmail(email).get().getId());
        return "user_info_edit";

    }
    @PostMapping("/user/info/edit")
    public String employeeEditProfile(
                             @Valid @ModelAttribute("userForm")UserForm userForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "user_info_view";
        }
        int userId = userRepo.findByEmail(userForm.getEmail()).get().getId();
        userForm.setId(userId);
        userService.updateUser(userForm,userId);
        redirectAttributes.addFlashAttribute("mess","Đổi thông tin thành công");
        return "redirect:/users";
    }




    @GetMapping("/admin/user/edit/{id}")
    public String editEmployeeForm(@PathVariable int id, Model model) {
        UserForm userForm = convertToUserForm(userService.getUserById(id));
        model.addAttribute("userForm", userForm);
//        model.addAttribute("role","admin");
        model.addAttribute("role","admin");
        return "admin_user_edit";
    }
    @PostMapping("/admin/user/edit/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userForm")UserForm userForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("error","Đã có lỗi xảy ra");
            return "redirect:/admin/user/edit/"+id;
        }
        // get student from database by id
        userForm.setId(id);
        if(userRepo.findByEmail(userForm.getEmail()).isPresent() && !userForm.getEmail().equals(userService.getUserById(id).getEmail())){
            redirectAttributes.addFlashAttribute("user", userForm);
            redirectAttributes.addFlashAttribute("error","Email này đã tồn tại");
            redirectAttributes.addFlashAttribute("role","admin");
            return "redirect:/admin/user/edit/"+id;
        }
        userService.updateUser(userForm,id);
        redirectAttributes.addFlashAttribute("mess","Đổi thông tin thành công");
        return "redirect:/users";
    }
    @GetMapping("/user/edit/password/{id}")
    public String editPasswordForm(@PathVariable int id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "user_edit_password";
    }
    @PostMapping("/user/edit/password/{id}")
    public String editPassword(@PathVariable int id,
                               @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm,
                               Model model,
                               RedirectAttributes redirectAttributes){

        if(!userService.checkOnlPassword(id,changePasswordForm.getOldPassword())){
            model.addAttribute("changePasswordForm", changePasswordForm);
            model.addAttribute("id", id);
            model.addAttribute("errorSamepass","Password cũ không đúng");
            return "user_edit_password";
        }
        userService.changePassword(id,changePasswordForm.getNewPassword());
        redirectAttributes.addFlashAttribute("mess","Đổi mật khẩu thành công");
        return "redirect:/users";
    }
    @GetMapping("/login")
    public String login(Model model) {
        LoginForm loginForm = new LoginForm();
        model.addAttribute(loginForm);
        return "login";
    }


//    @GetMapping("/login")
//    public String login(Model model){
//
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String login()

}
