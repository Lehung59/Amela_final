package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
//    List<User> getAllUser();

    User save(User user);

    void deleteUserById(int id);

    User getUserById(int id);

    User updateUser(User neuUser);
    Page<User> findPaginated(int pageNo, int pageSize);
}
