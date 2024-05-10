package com.example.demo.controller;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import com.example.demo.form.ChangePasswordForm;
import com.example.demo.form.UserForm;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
                           @RequestParam(defaultValue = "3") int size,
                           @ModelAttribute("successmess") String mess){

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
            if(!mess.isEmpty()) model.addAttribute("successmess",mess);
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
            return "redirect:/admin/insert";
        }

        userService.save(userForm);
        redirectAttributes.addFlashAttribute("successmessage","Da them thanh cong");
        return "redirect:/users";

    }
    @GetMapping("/user/active/{id}")
    public String activeUser(@PathVariable int id, Model model){
        userRepo.findById(id).ifPresent(
                user -> {user.setIsActived(true);
                userRepo.save(user);
                }
        );
        return "redirect:/users";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteEmployee(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("mess","Đã xóa thành công");
        return "redirect:/users";
    }
    @GetMapping("/user/view/{id}")
    public String viewEmployee(@PathVariable int id, Model model){
        model.addAttribute("user", userService.getUserById(id));
//        model.addAttribute("role","admin");
        return "admin_user_view";
    }


    @GetMapping("/user/edit/{id}")
    public String editEmployeeForm(@PathVariable int id, Model model,
                                   @ModelAttribute("error") String error) {
        UserForm userForm = convertToUserForm(userService.getUserById(id));
        model.addAttribute("userForm", userForm);
//        model.addAttribute("role","admin");
        model.addAttribute("role","admin");
        if(!error.isEmpty()) model.addAttribute("error",error);
        return "admin_user_edit";
    }
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userForm")UserForm userForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "admin_user_edit";
        }
        // get student from database by id
        userForm.setId(id);
        if(userRepo.findByEmail(userForm.getEmail()).isPresent() && !userForm.getEmail().equals(userService.getUserById(id).getEmail())){
            redirectAttributes.addFlashAttribute("user", userForm);
            redirectAttributes.addFlashAttribute("error","Email này đã tồn tại");
            redirectAttributes.addFlashAttribute("role","admin");
            return "redirect:/user/edit/"+id;
        }
        userService.updateUser(userForm,id);
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
                               Model model){
        String oldPassword = userRepo.findById(id).get().getPassword();
        String oldPasswordForm = changePasswordForm.getOldPassword();
        if(!oldPassword.equals(oldPasswordForm)){
            model.addAttribute("changePasswordForm", changePasswordForm);
            model.addAttribute("id", id);
            model.addAttribute("errorSamepass","Password cũ không đúng");
            return "user_edit_password";
        }


        userService.changePassword(id,changePasswordForm.getNewPassword());
        return "redirect:/user/edit/"+id;
    }


}
