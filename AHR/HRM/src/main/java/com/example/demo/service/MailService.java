package com.example.demo.service;

import com.example.demo.entity.Mail;

import java.util.List;

public interface MailService {

     List<Mail> getAllMail();
     Mail getMailById(int id);
     Mail saveMail(Mail mail);
     Mail updateMail(Mail mail);
     void deleteMail(int id);

}
