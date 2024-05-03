package com.example.demo.service.implement;

import com.example.demo.exception.MailNotFoundException;
import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepo;
import com.example.demo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements com.example.demo.service.MailService {

    private final MailRepo mailRepo;

    @Override
    public List<Mail> getAllMail() {
        return mailRepo.findAll();
    }

    @Override
    public Mail getMailById(int id) {
        Optional<Mail> mail = mailRepo.findById(id);
        if (mail.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + id);
        }
        return mail.get();
    }

    @Override
    public Mail saveMail(Mail mail) {
        return mailRepo.save(mail);
    }

    @Override
    public Mail updateMail(Mail mail) {
        Optional<Mail> oldObj = mailRepo.findById(mail.getMailId());
        if (oldObj.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + mail.getMailId());
        }
        Mail oldMail = oldObj.get();
        oldMail.setMailRecipient(mail.getMailRecipient());
        oldMail.setContent(mail.getContent());
        oldMail.setStatus(mail.getStatus());
        oldMail.setTitle(mail.getTitle());
        oldMail.setDateSend(mail.getDateSend());
        oldMail.setUpdatedAt(DateUtils.getCurrentDay());
//        oldMail.setUser(mail.getUser());
        return mailRepo.save(oldMail);
    }

    @Override
    public void deleteMail(int id) {
        Optional<Mail> oldObj = mailRepo.findById(id);
        if (oldObj.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + id);
        }
        mailRepo.deleteById(id);
    }
}
