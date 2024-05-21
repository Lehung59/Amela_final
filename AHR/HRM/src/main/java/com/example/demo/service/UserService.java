package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
//    List<User> getAllUser();

    void save(UserForm userForm);

    void saveUser(User user);

    void deleteUserById(int id);

    User getUserById(int id);

    void updateUser(UserForm userForm,int id);
    void changePassword(int id, String newPassword) throws Exception;

    Page<User> findPaginated(int pageNo, int pageSize);

    Optional<User> getUserByEmail(String email);
    UserForm getUserFormByEmail(String email);

    Optional<User> getUserByPhone(String phone);

    boolean checkOldPassword(int id,String oldPassword);

    void reSendCode(int id);

    void forgetPassword(User user, String email);

    void saveNewPassword(int id, String newPass);

    Set<String> getAllEmail();

    Page<User> getAllUserPaging(int page, int size, String keyword);

    String checkActiveToken(User user, String activeToken);
}
