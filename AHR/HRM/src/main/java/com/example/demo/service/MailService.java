package com.example.demo.service;

import com.example.demo.entity.Mail;
import com.example.demo.form.MailForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MailService {

     List<MailForm> getAllMail();
     MailForm getMailById(int id);
     void saveMail(MailForm mailForm);
     void updateMail(MailForm mailForm);
     void deleteMail(int id);

     Page<MailForm> findAllMailPaginable(Pageable pageable);
     Page<MailForm> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

     void draftMail(MailForm mailForm);
}
