package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    @Query("select o from User  o where o.email LIKE %?1%"
            + "or o.phoneNumber like %?1%"
            + "or o.address like %?1%"
            + "or CONCAT(o.firstName, ' ', o.lastName) LIKE %?1%")
    Page<User> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

//    Page<User> findByTitleContainingIgnoreCase(String keyword, Pageable paging);
}
