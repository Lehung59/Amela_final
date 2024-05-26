package com.example.demo.controller;

import com.example.demo.constant.Constants;
import com.example.demo.entity.Token;
import com.example.demo.entity.User;
import com.example.demo.form.ChangePasswordForm;
import com.example.demo.form.LoginForm;
import com.example.demo.form.SearchForm;
import com.example.demo.form.UserForm;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.TokenService;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.service.implement.UserServiceImpl.convertToUserForm;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final UserUtils userUtils;


    @GetMapping("/users")
    public String listUser(Model model,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue =Constants.PAGE) int page,
                           @RequestParam(defaultValue = Constants.SIZE) int size
    ) {

        try {
            Page<User> pageTuts = userService.getAllUserPaging(page,size,keyword);
            if(keyword != null) {
                model.addAttribute("keyword", keyword);

            }
            SearchForm searchForm = SearchForm.builder()
                    .keyword(keyword)
                    .size(size)
                    .page(page)
                    .build();
            model.addAttribute("searchForm", searchForm);
            model.addAttribute("users", pageTuts.getContent());
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
    public String addEmployee(Model model) {

        User user = new User();
        model.addAttribute("user", user);
        return "admin_user_add";
    }

    @PostMapping("/admin/insert")
    public String saveEmployee(@Valid @ModelAttribute("user") UserForm userForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        try{
            if (bindingResult.hasErrors()) {
                Map<String, String> errors= new HashMap<>();

                bindingResult.getFieldErrors().forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );
                redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());
                return "redirect:/admin/insert?error";
            }
            userService.save(userForm);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Successfully added user");
            return "redirect:/users";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/admin/insert";
        }


    }

    @GetMapping("/user/active")
    public String activeUser(@RequestParam("email") String encodedEmail,
                             @RequestParam("activeToken") String encodedActiveToken,
                             RedirectAttributes redirectAttributes) {
        try {
            // Giải mã email và active code
            String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString());
            String activeToken = URLDecoder.decode(encodedActiveToken, StandardCharsets.UTF_8.toString());

//            String newActiveCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
            // Tìm user bằng email đã giải mã
            Optional<User> user = userService.getUserByEmail(email);

            if (user.isPresent()) {
                User newUser = user.get();
                String resultCheckActiveToken = userService.checkActiveToken(newUser, activeToken);
                switch (resultCheckActiveToken) {
                    case Constants.INVALID_TOKEN_ERROR:
                        redirectAttributes.addFlashAttribute(Constants.ERROR, Constants.INVALID_TOKEN_ERROR_MESS);
                        return "redirect:/login?errors=invalid";
                    case Constants.EMAIL_ERROR:
                        redirectAttributes.addFlashAttribute(Constants.ERROR, Constants.EMAIL_ERROR_MESS);
                        return "redirect:/login?errors=email";
                    case Constants.REVOKED_TOKEN_ERROR:
                        redirectAttributes.addFlashAttribute(Constants.ERROR, Constants.REVOKED_TOKEN_ERROR_MESS);
                        return "redirect:/login?errors=revoked";
                    case Constants.TIME_ERROR:
                        redirectAttributes.addFlashAttribute(Constants.ERROR, Constants.TIME_ERROR_MESS);
                        return "redirect:/login?errors=time";
                    case Constants.VERIFIED:
                        redirectAttributes.addFlashAttribute(Constants.SUCCESS, Constants.VERIFIED_MESS);
                        return "redirect:/login?verified";

                    case Constants.VALID:
                        redirectAttributes.addFlashAttribute(Constants.SUCCESS, Constants.VALID_MESS);
                        return "redirect:/login?valid";
                }
            }
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here, such as logging the error or sending an error message back to the client
            redirectAttributes.addFlashAttribute(Constants.ERROR, Constants.EXCEPTION_ERROR_MESS);
            return "redirect:/login?errors=server";
        }
    }

    @GetMapping("/admin/sendcode/{id}")
    public String reSendCode(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.reSendCode(id);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Code sent successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
        }
        return "redirect:/users";
    }


    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }
//    @GetMapping("/admin/delete/re")
//    public String deleteEmployee(RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute(Constants.SUCCESS,"Xoá thành công người dùng");
//        return "redirect:/admin/users";
//    }

    @GetMapping("/user/view/{id}")
    public String viewEmployee(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
//        int id = userUtils.getUserNow().getId();
        try {
            model.addAttribute("user", userService.getUserById(id));
            return "admin_user_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/users";
        }
    }
//        model.addAttribute("role","admin");


    @GetMapping("/user/info/view")
    public String employeeViewProfile(Model model) {
        String email = userUtils.getUserName();
        model.addAttribute("userForm", userService.getUserFormByEmail(email));
        model.addAttribute("id", userService.getUserByEmail(email).get().getId());
        return "user_info_view";

    }

    @GetMapping("/user/info/edit")
    public String employeeEditProfile(Model model, RedirectAttributes redirectAttributes) {
        try{
            String email = userUtils.getUserName();
            UserForm userform = userService.getUserFormByEmail(email);
            model.addAttribute("userForm",userform );
            model.addAttribute("id", userService.getUserByEmail(email).get().getId());
            return "user_info_edit";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR, e.getMessage());
            return "redirect:/user/info/view";
        }


    }

    @PostMapping("/user/info/edit")
    public String employeeEditProfile(
            @Valid @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) throws Exception {
        try{
            if (bindingResult.hasErrors()) {
                Map<String, String> errors= new HashMap<>();

                bindingResult.getFieldErrors().forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );
                redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());
                return "redirect:/user/info/edit";
            }
            int userId = userService.getUserByEmail(userForm.getEmail()).get().getId();
            userForm.setId(userId);
            userForm.setActive(true);
            userService.updateUser(userForm);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Changed information successfully");
            return "redirect:/users";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute(Constants.ERROR,e.getMessage());
            return "redirect:/user/info/edit";
        }
    }

    @GetMapping("/user/forgetpassword")
    public String employeeForgetPassword(Model model) {
        return "user_forget_password";
    }

    @GetMapping("/user/forgetpassword/{email}")
    public String employeeForgetPassword(@PathVariable String email,
                                         RedirectAttributes redirectAttributes) {
        System.out.println(email);
        Optional<User> userOptional = userService.getUserByEmail(email);
        redirectAttributes.addFlashAttribute("email",email);
        if(userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(Constants.ERROR,"Not available at this email");
        } else {
            User user = userOptional.get();
            userService.forgetPassword(user, email);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS,"Email sent!");

        }
        return "redirect:/user/forgetpassword";
    }
    @GetMapping("/user/resetpassword")
    public String employeeForgetPasswordCheck(@RequestParam String token,
                                              RedirectAttributes redirectAttributes,
                                              Model model){
        Optional<Token> tokenPasswordOptional = tokenService.findByToken(token, Token.TokenType.PASSWORD);
        if(tokenPasswordOptional.isEmpty()){
            redirectAttributes.addFlashAttribute(Constants.ERROR,"The code is incorrect or has expired");
            return "redirect:/user/forgetpassword";

        } else {
            Token tokenPassword = tokenPasswordOptional.get();
            tokenService.revokeToken(tokenPassword);
            int userId = tokenPassword.getUser().getId();
            model.addAttribute("userId",userId);
            return "user_changepassword";
        }
    }
    @PostMapping("/user/resetpassword/")
    public String employeeForgetPasswordCheck(@RequestParam int id,
                                              @RequestParam String newPass,
                                              RedirectAttributes redirectAttributes){
        userService.saveNewPassword(id,newPass);
        redirectAttributes.addFlashAttribute(Constants.SUCCESS,"Password changed successfully");
        return "redirect:/login";
    }




    @GetMapping("/admin/user/edit/{id}")
    public String editEmployeeForm(@PathVariable int id, Model model) {
        UserForm userForm = convertToUserForm(userService.getUserById(id));
        model.addAttribute("userForm", userForm);
//        model.addAttribute("role","admin");
//        model.addAttribute("role", "admin");
        return "admin_user_edit";
    }

    @PostMapping("/admin/user/edit/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userForm") UserForm userForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors= new HashMap<>();

            bindingResult.getFieldErrors().forEach(
                    error -> errors.put(error.getField(), error.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());

            return "redirect:/admin/user/edit/" + id;
        }
        // get student from database by id
        userForm.setId(id);
        if (userService.getUserByEmail(userForm.getEmail()).isPresent() && !userForm.getEmail().equals(userService.getUserById(id).getEmail())) {
            redirectAttributes.addFlashAttribute("user", userForm);
            redirectAttributes.addFlashAttribute(Constants.ERROR, "This email already exists");
            redirectAttributes.addFlashAttribute("role", "admin");
            return "redirect:/admin/user/edit/" + id;
        }
        userService.updateUser(userForm);
        redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Changed information successfully");
        return "redirect:/users";
    }

    @GetMapping("/user/edit/password")
    public String editPasswordForm( Model model) {
        int id = userUtils.getUserNow().getId();
        model.addAttribute("id", id);
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "user_edit_password";
    }

    @PostMapping("/user/edit/password")
    public String editPassword(
                               @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        try{
            int id = userUtils.getUserNow().getId();
            if (!userService.checkOldPassword(id, changePasswordForm.getOldPassword())) {
                model.addAttribute("changePasswordForm", changePasswordForm);
                model.addAttribute("id", id);
                model.addAttribute("errorSamepass", "The old password is incorrect");
                return "user_edit_password";
            }
            userService.changePassword(id, changePasswordForm.getNewPassword());
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Password changed successfully");
            return "redirect:/users";
        }catch (Exception ex){
            redirectAttributes.addFlashAttribute(Constants.ERROR, ex);

            return "redirect:/users";
        }

    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginForm loginForm = new LoginForm();
        model.addAttribute(loginForm);
        return "login";
    }

}
