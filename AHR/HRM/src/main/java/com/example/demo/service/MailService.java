package com.example.demo.service;

import com.example.demo.entity.Mail;
import com.example.demo.form.MailForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MailService {

     List<MailForm> getAllMail();
     MailForm getMailById(int id);
     void saveMail(MailForm mailForm);
     void updateMail(MailForm mailForm);
     void deleteMail(int id);

     Page<MailForm> findAllMailPaginable(Pageable pageable);
     Page<MailForm> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

     Optional<Mail> checkExsist(Integer id);

     void draftNewMail(MailForm mailForm);

     void draftExistMail(MailForm mailForm);
}
