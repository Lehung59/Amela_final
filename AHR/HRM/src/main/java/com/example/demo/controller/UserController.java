package com.example.demo.controller;

import com.example.demo.entity.User;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepo userRepo;


    @GetMapping("/users")
    public String listUser(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size){

        try {
            Pageable paging = PageRequest.of(page - 1, size);
            Page<User> pageTuts= userRepo.findAll(paging);;
            List<User> users= pageTuts.getContent();

            model.addAttribute("users", users);
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

//        model.addAttribute("users",userService.getAllUser());
        return "users";
    }
    @GetMapping("/admin/insert")
    public String addEmployee(Model model ){

        User user = new User();
        model.addAttribute("user",user);
        return "user_add";
    }
    @PostMapping("/admin/insert")
    public String saveEmployee(@Valid @ModelAttribute("user")User user,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "user_add";
        }

        userService.save(user);
        return "redirect:/users";

    }
    @GetMapping("/admin/delete/{id}")
    public String deleteEmployee(@PathVariable int id) {
        userService.deleteUserById(id);
        return "redirect:/users";
    }
    @GetMapping("/user/edit/{id}")
    public String editEmployeeForm(@PathVariable int id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "edit_user";
    }
    @PostMapping("/user/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("user")User user,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "edit_user";
        }
        // get student from database by id
        user.setId(id);
        userService.updateUser(user);

//        User exUser = userService.getUserById(id);
//        exUser.setId(id);
//        exUser.setFirstName(user.getFirstName());
//        exUser.setLastName(user.getLastName());
//        exUser.setEmail(user.getEmail());
//        exUser.setMale();
//        // save updated student object
//        userRepo.save(exUser);
        return "redirect:/users";
    }

//    @GetMapping("/page/{pageNo}")
//    public String findPaginated(@PathVariable("pageNo") int pageNo, Model model){
//        int pageSize=5;
//        Page<User> page = userService.findPaginated(pageNo,pageSize);
//        List<User> users= page.getContent();
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPage",page.getTotalPages());
//        model.addAttribute("totalItem", page.getTotalElements());
//        model.addAttribute("users",users);
//        return "users";
//
//    }
//    @GetMapping("/")
//    public String viewHomePage(Model model) {
//        return findPaginated(1, model);
//    }



}
