package com.example.demo;


import com.example.demo.entity.Mail;
import com.example.demo.entity.MailStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.MailRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class MailRepoTest {
    @Autowired
    private MailRepo mailRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateUser(){
        Mail mail = new Mail();
        mail.setMailId(10);
        mail.setMailRecipient("le@gmail.com");
//        User user = userRepo.findById(2).get();
//        mail.getUsers().add(listU.get());
        mail.setStatus(MailStatus.DRAFT);
        List<User> users = userRepo.findAll();
        mail.setUsers(users);
//        User user = new User();
//        user.setEmail("nguyenlehungsc@gmail.com");
//        user.setPassword("123456");
//        user.setFirstName("le");
//        user.setPassword("123456");
        mail.setCreatedAt(DateUtils.getCurrentDay());
          mailRepo.save(mail);
//        User existUser = testEntityManager.find(User.class, savedUser.getId());
//        assertThat(existUser.getEmail()).isEqualTo(savedUser);
    }

}
