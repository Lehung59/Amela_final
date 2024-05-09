package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
//    List<User> getAllUser();

    void save(UserForm userForm);

    void deleteUserById(int id);

    User getUserById(int id);

    void updateUser(UserForm userForm,int id);
    void changePassword(int id, String newPassword);

    Page<User> findPaginated(int pageNo, int pageSize);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPhone(String phone);
}
