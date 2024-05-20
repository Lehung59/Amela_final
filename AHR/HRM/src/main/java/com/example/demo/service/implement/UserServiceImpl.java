package com.example.demo.service.implement;

import com.example.demo.entity.Role;
import com.example.demo.entity.TokenPassword;
import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.repository.TokenPasswordRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.TokenPasswordService;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.EmailMix;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenPasswordService tokenPasswordService;


//    @Override
//    public List<User> getAllUser() {
//        return userRepo.findAll(Sort.by(Sort.Direction.ASC),"id");
//    }

    @Override
    public void updateUser(UserForm userForm, int id) {


        User newUser = convertToUser(userForm);
        User exUser = userRepo.findById(id).get();
        exUser.setLastName(newUser.getLastName());
        exUser.setFirstName(newUser.getFirstName());
        exUser.setPhoneNumber(newUser.getPhoneNumber());
        exUser.setIsActived(newUser.getIsActived());
        if(!newUser.getEmail().equals(exUser.getEmail())){
            String activeCode = UUID.randomUUID().toString().substring(0,10).toUpperCase();
            exUser.setActiveCode(activeCode);
            exUser.setCodeExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000));

            exUser.setEmail(newUser.getEmail());
            exUser.setIsActived(false);
            String recipientAddress = userForm.getEmail();


            String subject = "Xác nhận tài khoản";
            String urlBase = "http://localhost:8080/user/active";

            try{
                String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
                String encodedActiveCode = URLEncoder.encode(activeCode, StandardCharsets.UTF_8.toString());
                String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeCode=" + encodedActiveCode;
                String message = "<p>Tài khoản được khởi tạo từ Admin LeHung.</p>" +
                        "<p>Tên tài khoản nhân viên: " + recipientAddress + ".</p>" +
                        "<p>Mã kích hoạt có hiệu lực trong 15 phút.</p>" +
                        "<p>Nhấp vào nút sau để xác nhận đăng ký tài khoản:</p>" +
                        "<a href='" + confirmationUrl + "' style='padding: 10px 20px; font-size: 16px; color: white; background-color: #333333; border: none; border-radius: 5px; cursor: pointer; text-decoration: none;'>Xác nhận tài khoản</a>";
                EmailMix e = new EmailMix("nguyenlehungsc1@gmail.com", "xcsslxxwycaillbg",0);
                e.sendContent(recipientAddress,subject,message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // Thông báo lỗi hoặc xử lý phù hợp
            }
        }
        exUser.setAddress(newUser.getAddress());
        exUser.setAvatar(newUser.getAvatar());
        exUser.setBirthday(newUser.getBirthday());
        exUser.setUpdatedAt(DateUtils.getCurrentDay());
        exUser.setMale(newUser.isMale());
//        exUser.setIsActived(newUser.getIsActived());
        updateSecurityContextHolder(exUser);

        userRepo.save(exUser);

    }

    @Override
    public void save(UserForm userForm) {

        String activeCode = UUID.randomUUID().toString().substring(0,10).toUpperCase();

        User user = convertToUser(userForm);
        String recipientAddress = userForm.getEmail();
        User newObj = new User();
        newObj.setEmail(user.getEmail());
        newObj.setPassword(passwordEncoder.encode(user.getPhoneNumber()));
        newObj.setLastName(user.getLastName());
        newObj.setFirstName(user.getFirstName());
        newObj.setPhoneNumber(user.getPhoneNumber());
        newObj.setAddress(user.getAddress());
        newObj.setAvatar(user.getAvatar());
        newObj.setBirthday(user.getBirthday());
        newObj.setRole(Role.EMPLOYEE);
        newObj.setIsActived(false);
        newObj.setMale(false);
        newObj.setActiveCode(activeCode);
        newObj.setCodeExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000));
        newObj.setCreatedAt(DateUtils.getCurrentDay());
        newObj.setUpdatedAt(DateUtils.getCurrentDay());
        if (user.isMale()) {
            newObj.setMale(true);
        }

        userRepo.save(newObj);
        String password = userRepo.findByEmail(recipientAddress).get().getPhoneNumber();
        String subject = "Xác nhận tài khoản";
        String urlBase = "http://localhost:8080/user/active";
        try{
            String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
            String encodedActiveCode = URLEncoder.encode(activeCode, StandardCharsets.UTF_8.toString());
            String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeCode=" + encodedActiveCode;

//
//        String confirmationUrl
//                =   urlBase+"?email=" +recipientAddress + "&activeCode=" + activeCode;
        String message = "<p>Tài khoản được khởi tạo từ Admin LeHung.</p>" +
                "<p>Tên tài khoản nhân viên: " + recipientAddress + ".</p>" +
                "<p>Mật khẩu của bạn: <strong>" + password + "</strong>.</p>" +
                "<p>Mã kích hoạt có hiệu lực trong 15 phút.</p>" +
                "<p>Nhấp vào nút sau để xác nhận đăng ký tài khoản:</p>" +
                "<a href='" + confirmationUrl + "' style='padding: 10px 20px; font-size: 16px; color: white; background-color: #333333; border: none; border-radius: 5px; cursor: pointer; text-decoration: none;'>Xác nhận tài khoản</a>";
        EmailMix e = new EmailMix("nguyenlehungsc1@gmail.com", "xcsslxxwycaillbg",0);
        e.sendContent(recipientAddress,subject,message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Thông báo lỗi hoặc xử lý phù hợp
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
    public void changePassword(int id, String newPassword){
        User user = userRepo.findById(id).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

    }
    @Override
    public boolean checkOnlPassword(int id,String oldPassword){
        String userPass = userRepo.findById(id).get().getPassword();

        return passwordEncoder.matches(oldPassword, userPass);
    }

    @Override
    public void reSendCode(int id) {
        User user = userRepo.findById(id).get();
        String recipientAddress = user.getEmail();
        String activeCode = user.getActiveCode();
        user.setCodeExpried(new Date(DateUtils.getCurrentDay().getTime() + 900000));
        userRepo.save(user);
        String subject = "Xác nhận tài khoản";
        String urlBase = "http://localhost:8080/user/active";
        try{
            String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
            String encodedActiveCode = URLEncoder.encode(activeCode, StandardCharsets.UTF_8.toString());
            String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeCode=" + encodedActiveCode;

//
//        String confirmationUrl
//                =   urlBase+"?email=" +recipientAddress + "&activeCode=" + activeCode;
            String message = "<p>Tài khoản được khởi tạo từ Admin LeHung.</p>" +
                    "<p>Tên tài khoản nhân viên: " + recipientAddress + ".</p>" +
                    "<p>Mã kích hoạt có hiệu lực trong 15 phút.</p>" +
                    "<p>Nhấp vào nút sau để xác nhận đăng ký tài khoản:</p>" +
                    "<a href='" + confirmationUrl + "' style='padding: 10px 20px; font-size: 16px; color: white; background-color: #333333; border: none; border-radius: 5px; cursor: pointer; text-decoration: none;'>Xác nhận tài khoản</a>";
            EmailMix e = new EmailMix("nguyenlehungsc1@gmail.com", "xcsslxxwycaillbg",0);
            e.sendContent(recipientAddress,subject,message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Thông báo lỗi hoặc xử lý phù hợp
        }
    }

    @Override
    public void forgetPassword(User user, String email) {


        String activeCode = tokenPasswordService.createToken(user).getToken();
        String recipientAddress = user.getEmail();
        String subject = "Xác nhận tài khoản";
        String urlBase = "http://localhost:8080/user/active";
        try{
            String encodedEmail = URLEncoder.encode(recipientAddress, StandardCharsets.UTF_8.toString());
            String encodedActiveCode = URLEncoder.encode(activeCode, StandardCharsets.UTF_8.toString());
            String confirmationUrl = urlBase + "?email=" + encodedEmail + "&activeCode=" + encodedActiveCode;

//
//        String confirmationUrl
//                =   urlBase+"?email=" +recipientAddress + "&activeCode=" + activeCode;
            String message = "<p>Tài khoản được khởi tạo từ Admin LeHung.</p>" +
                    "<p>Tên tài khoản nhân viên: " + recipientAddress + ".</p>" +
                    "<p>Mã kích hoạt có hiệu lực trong 15 phút.</p>" +
                    "<p>Mã kích hoạt của bạn là: <b>" + activeCode + "</b> </p>";
            EmailMix e = new EmailMix("nguyenlehungsc1@gmail.com", "xcsslxxwycaillbg",0);
            e.sendContent(recipientAddress,subject,message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Thông báo lỗi hoặc xử lý phù hợp
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
        Pageable paging = PageRequest.of(page - 1, size);
        Page<User> pageTuts = userRepo.findAll(paging);
        if (keyword == null) {
            pageTuts = userRepo.findAll(paging);
        } else {
            pageTuts = userRepo.findByTitleContainingIgnoreCase(keyword, paging);
        }
        return pageTuts;
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
