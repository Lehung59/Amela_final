package com.example.demo.service.implement;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.UserService;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.EmailMix;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;




@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;


//    @Override
//    public List<User> getAllUser() {
//        return userRepo.findAll(Sort.by(Sort.Direction.ASC),"id");
//    }

    @Override
    public void updateUser(User newUser) {
        User exUser = userRepo.findById(newUser.getId()).get();
        if(newUser.getEmail() != exUser.getEmail()){
            exUser.setEmail(newUser.getEmail());
        }
        exUser.setLastName(newUser.getLastName());
        exUser.setFirstName(newUser.getFirstName());
        exUser.setPhoneNumber(newUser.getPhoneNumber());
        exUser.setAddress(newUser.getAddress());
        exUser.setAvatar(newUser.getAvatar());
        exUser.setBirthday(newUser.getBirthday());
        exUser.setUpdatedAt(DateUtils.getCurrentDay());

        exUser.setMale(false);
        if (newUser.isMale()) {
            exUser.setMale(true);
        } else exUser.setMale(false);
        userRepo.save(exUser);

    }

    @Override
    public void save(UserForm userForm) {
        User user = convertToUser(userForm);
        String recipientAddress = userForm.getEmail();
        User newObj = new User();
        newObj.setEmail(user.getEmail());
        newObj.setPassword(user.getPhoneNumber());
        newObj.setLastName(user.getLastName());
        newObj.setFirstName(user.getFirstName());
        newObj.setPhoneNumber(user.getPhoneNumber());
        newObj.setAddress(user.getAddress());
        newObj.setAvatar(user.getAvatar());
        newObj.setBirthday(user.getBirthday());
        newObj.setRole(Role.EMPLOYEE);
        newObj.setIsActived(false);
        newObj.setMale(false);
        newObj.setCreatedAt(DateUtils.getCurrentDay());
        newObj.setUpdatedAt(DateUtils.getCurrentDay());
        if (user.isMale()) {
            newObj.setMale(true);
        }

        userRepo.save(newObj);
        String password = userRepo.findByEmail(recipientAddress).get().getPassword();
        String subject = "Xác nhận tài khoản";
        String confirmationUrl
                =   "http://localhost:8080/user/active/"+userRepo.findByEmail(recipientAddress).get().getId();
        String message = "<p>Tài khoản được khởi tạo từ Admin LeHung.</p>" +
                "<p>Tên tài khoản nhân viên : " + recipientAddress + ".</p>"
                + "<p>Mật khẩu của bạn: <strong>" + password + "</strong>.</p>"
                + "<p>Nhấp vào nút sau để xác nhận đăng ký tài khoản:</p>"
                + "<form action='" + confirmationUrl + "' method='get'>"
                + "<button type='submit' style='padding: 10px 20px; font-size: 16px; color: white; background-color: #333333; border: none; border-radius: 5px; cursor: pointer;'>"
                + "Xác nhận tài khoản</button>"
                + "</form>";
        EmailMix e = new EmailMix("nguyenlehungsc1@gmail.com", "xcsslxxwycaillbg",0);
        e.sendContentToVer2(recipientAddress,subject,message);
    }

    @Override
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
        user.setMale(userForm.isMale());
        user.setBirthday(userForm.getBirthday());
        user.setCreatedAt(userForm.getCreatedAt());
        user.setUpdatedAt(userForm.getUpdatedAt());
        user.setAttendances(userForm.getAttendances()); // Nếu cần tất cả thông tin về Attendance
        return user;
    }
}
