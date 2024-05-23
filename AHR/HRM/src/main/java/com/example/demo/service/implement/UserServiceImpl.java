package com.example.demo.service.implement;

import com.example.demo.constant.Constants;
import com.example.demo.entity.Role;
import com.example.demo.entity.Token;
import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.repository.TokenRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.ImageUpload;
import com.example.demo.utils.MailUtils;
import com.example.demo.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TokenRepo tokenRepo;
    private final UserUtils userUtils;
    private final TemplateEngine templateEngine;
    private final ResourceLoader resourceLoader;
    private final MailUtils mailUtils;

    private final ThreadPoolTaskScheduler taskScheduler;
    private final ImageUpload imageUpload;


//    @Override
//    public List<User> getAllUser() {
//        return userRepo.findAll(Sort.by(Sort.Direction.ASC),"id");
//    }

    @Override
    public void updateUser(UserForm userForm) throws Exception {

        int id = userForm.getId();
        User newUser = convertToUser(userForm);
        User exUser = userRepo.findById(id).get();
        String avt = imageUpload.upload(userForm.getAvatarFile());
        boolean changeMail = !newUser.getEmail().equals(exUser.getEmail());
        exUser.setLastName(newUser.getLastName());
        exUser.setFirstName(newUser.getFirstName());
        exUser.setPhoneNumber(newUser.getPhoneNumber());
        exUser.setIsActived(newUser.getIsActived());
        exUser.setAddress(newUser.getAddress());
        exUser.setAvatar(avt);
        exUser.setBirthday(newUser.getBirthday());
        exUser.setUpdatedAt(DateUtils.getCurrentDay());
        exUser.setMale(newUser.isMale());
        exUser.setEmail(newUser.getEmail());
        exUser = userRepo.save(exUser);
        if (changeMail) {
            Token token = tokenService.createToken(exUser, Token.TokenType.ACTIVE);
            exUser.setIsActived(false);
            tokenRepo.save(token);

            String recipientAddress = userForm.getEmail();
            String subject = "Xác nhận tài khoản";
            String urlBase = "http://localhost:8080/user/active";

            try{
                String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
                String encodedActiveCode = URLEncoder.encode(token.getToken(), StandardCharsets.UTF_8.toString());
                String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeToken=" + encodedActiveCode;
                String content = mailUtils.loadActivationEmailTemplate(recipientAddress,confirmationUrl,"email-active");
                mailUtils.sendContent(recipientAddress,subject,content);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // Thông báo lỗi hoặc xử lý phù hợp
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        updateSecurityContextHolder(exUser);
        userRepo.save(exUser);

    }

    @Override
    public void save(UserForm userForm) {

//        String activeCode = UUID.randomUUID().toString().substring(0,10).toUpperCase();

        User user = convertToUser(userForm);
        String recipientAddress = userForm.getEmail();
        User newObj = new User();
        newObj.setEmail(user.getEmail());
        newObj.setPassword(passwordEncoder.encode(user.getPhoneNumber()));
        newObj.setLastName(user.getLastName());
        newObj.setFirstName(user.getFirstName());
        newObj.setPhoneNumber(user.getPhoneNumber());
        newObj.setAddress(user.getAddress());
        newObj.setAvatar(Constants.DEFAULT_AVATAR);
        newObj.setBirthday(user.getBirthday());
        newObj.setRole(Role.EMPLOYEE);
        newObj.setIsActived(false);
        newObj.setMale(false);
//        newObj.setActiveCode(activeCode);
//        newObj.setCodeExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000));
        newObj.setCreatedAt(DateUtils.getCurrentDay());
        newObj.setUpdatedAt(DateUtils.getCurrentDay());
        if (user.isMale()) {
            newObj.setMale(true);
        }

        User newU = userRepo.save(newObj);
        Token activeToken = tokenService.createToken(newU, Token.TokenType.ACTIVE);

        String password = userRepo.findByEmail(recipientAddress).get().getPhoneNumber();
        String subject = "Xác nhận tài khoản";
        String urlBase = "http://localhost:8080/user/active";
        try{
            String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
            String encodedActiveCode = URLEncoder.encode(activeToken.getToken(), StandardCharsets.UTF_8.toString());
            String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeToken=" + encodedActiveCode;

          String content = mailUtils.loadActivationEmailTemplate(recipientAddress,confirmationUrl,"email-newuser",password);
          mailUtils.sendContent(recipientAddress,subject,content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        User user = userRepo.findById(id).get();
        userRepo.delete(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id).get();
    }



    @Override
    public Page<User> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.userRepo.findAll(pageable);

    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public UserForm getUserFormByEmail(String email) {
        User user = userRepo.findByEmail(email).get();
        return convertToUserForm(user);

    }

    @Override
    public Optional<User> getUserByPhone(String phone) {
        return userRepo.findByPhoneNumber(phone);
    }


    @Override
    public void changePassword(int id, String newPassword) throws Exception {
        User user = userRepo.findById(id).get();
        if(!user.getEmail().equals(userUtils.getUserName())){
            throw new Exception("Ban khong duoc thay doi thong tin nay");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

    }
    @Override
    public boolean checkOldPassword(int id,String oldPassword){
        String userPass = userRepo.findById(id).get().getPassword();

        return passwordEncoder.matches(oldPassword, userPass);
    }

    @Override
    public void reSendCode(int id) {
        User user = userRepo.findById(id).get();
        Token activeToken = tokenService.createToken(user, Token.TokenType.ACTIVE);

        String recipientAddress = user.getEmail();
//        String activeCode = user.getActiveCode();
//        user.setCodeExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000));
        userRepo.save(user);
        String subject = "Xác nhận tài khoản";
        String urlBase = "http://localhost:8080/user/active";
        try{
            String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
            String encodedActiveCode = URLEncoder.encode(activeToken.getToken(), StandardCharsets.UTF_8.toString());
            String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeToken=" + encodedActiveCode;

            String content = mailUtils.loadActivationEmailTemplate(recipientAddress,confirmationUrl,"email-avtice" );
            mailUtils.sendContent(recipientAddress,subject,content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forgetPassword(User user, String email) {


        String activeCode = tokenService.createToken(user, Token.TokenType.PASSWORD).getToken();
        String recipientAddress = user.getEmail();
        String subject = "Xác nhận tài khoản";
        try{
            String content = mailUtils.loadActivationEmailTemplate(recipientAddress,activeCode,"email-forgetpassword");
            mailUtils.sendContent(recipientAddress,subject,content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveNewPassword(int id, String newPass) {
        User user = userRepo.findById(id).get();
        user.setPassword(passwordEncoder.encode(newPass));
        userRepo.save(user);

    }

    @Override
    public Set<String> getAllEmail() {
        return userRepo.getAllEmail();
    }

    @Override
    public Page<User> getAllUserPaging(int page, int size, String keyword) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<User> pageTuts = userRepo.findAll(paging);
        if (keyword == null) {
            pageTuts = userRepo.findAll(paging);
        } else {
            pageTuts = userRepo.findByTitleContainingIgnoreCase(keyword, paging);
        }
        return pageTuts;
    }

    @Override
    public String checkActiveToken(User user, String activeToken) {
        Optional<Token> token = tokenRepo.findByToken(activeToken, Token.TokenType.ACTIVE);
        if (token.isPresent()) {
            Token t = token.get();
            if(!t.getUser().getEmail().equals(user.getEmail())) return "errors=email";

            if(t.isRevoked()) return "errors=revoked";

            if(t.getTokenExpried().before(DateUtils.getCurrentDay())) return "errors=time";

            t.setRevoked(true);
            tokenRepo.save(t);
            //tai khoan da duoc kich hoat san
            if(t.getUser().getIsActived()) return "verified";

            User u = t.getUser();
            u.setIsActived(true);
            userRepo.save(u);
            return "valid";
        }
        return "errors=invalid";
    }

    public static UserForm convertToUserForm(User user) {
        UserForm userForm = new UserForm();
        userForm.setId(user.getId());
        userForm.setEmail(user.getEmail());
        userForm.setPassword(user.getPassword());
        userForm.setFirstName(user.getFirstName());
        userForm.setLastName(user.getLastName());
        userForm.setPhoneNumber(user.getPhoneNumber());
        userForm.setAddress(user.getAddress());
        userForm.setAvatar(user.getAvatar());
        userForm.setRole(user.getRole());
        userForm.setMale(user.isMale());
        userForm.setActive(user.getIsActived());
        userForm.setBirthday(user.getBirthday());
        userForm.setCreatedAt(user.getCreatedAt());
        userForm.setUpdatedAt(user.getUpdatedAt());
        userForm.setAttendances(user.getAttendances()); // Nếu cần tất cả thông tin về Attendance
        return userForm;
    }

    // Chuyển từ UserForm sang User
    public static User convertToUser(UserForm userForm) {
        User user = new User();
        user.setId(userForm.getId());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setAddress(userForm.getAddress());
        user.setAvatar(userForm.getAvatar());
        user.setRole(userForm.getRole());
        user.setIsActived(userForm.isActive());
        user.setMale(userForm.isMale());
        user.setBirthday(userForm.getBirthday());
        user.setCreatedAt(userForm.getCreatedAt());
        user.setUpdatedAt(userForm.getUpdatedAt());
        user.setAttendances(userForm.getAttendances()); // Nếu cần tất cả thông tin về Attendance
        return user;
    }

    public void updateSecurityContextHolder(User newUser){
        CustomUserDetails customUserDetails = new CustomUserDetails(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                customUserDetails.getPassword(),
                customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }




}
