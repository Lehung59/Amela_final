package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
//    List<User> getAllUser();

    void save(User user);

    void deleteUserById(int id);

    User getUserById(int id);

    void updateUser(User neuUser);
    Page<User> findPaginated(int pageNo, int pageSize);
}
