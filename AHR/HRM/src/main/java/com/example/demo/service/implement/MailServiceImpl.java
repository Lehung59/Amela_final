package com.example.demo.service.implement;

import com.example.demo.entity.User;
import com.example.demo.form.MailForm;
import com.example.demo.exception.MailNotFoundException;
import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements com.example.demo.service.MailService {

    private final MailRepo mailRepo;
    private final UserRepo userRepo;

    @Override
    public List<MailForm> getAllMail() {
        return mailRepo.findAll().stream()
                .map(this::convertToMailDto)
                .collect(Collectors.toList());
    }

    @Override
    public MailForm getMailById(int id) {
        Optional<Mail> mail = mailRepo.findById(id);
        if (mail.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + id);
        }
        return convertToMailDto(mail.get()) ;
    }

    @Override
    public void  saveMail(MailForm mailForm) {
        Mail mail = convertToMail(mailForm);
        mailRepo.save(mail);
    }

    @Override
    public void updateMail(MailForm mailForm) {
        Mail mail = convertToMail(mailForm);
        Optional<Mail> oldObj = mailRepo.findById(mail.getMailId());
        mailRepo.deleteAllUsersByMailId(mail.getMailId());

        String listMails = mailForm.getMailRecipient().replaceAll("\\s+", "");
        List<String> emailList = Arrays.asList(listMails.split(","));
        List<User> foundUsers = new ArrayList<>();
        for (String email : emailList) {
            Optional<User> userOptional = userRepo.findByEmail(email);
            if(userOptional.isEmpty()) throw new MailNotFoundException("Mail not found:" + email);
            userOptional.ifPresent(foundUsers::add);
        }

        if (oldObj.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + mail.getMailId());
        }
        Mail oldMail = oldObj.get();
        oldMail.getUsers().clear();

        // Thêm người dùng mới vào danh sách liên kết
        oldMail.getUsers().addAll(foundUsers);
//        List<User> usersToRemove = new ArrayList<>();
//        for (User user : oldMail.getUsers()) {
//            if (!foundUsers.contains(user)) {
//                usersToRemove.add(user);
//            }
//        }
//        oldMail.getUsers().removeAll(usersToRemove);

        // Thêm danh sách người dùng mới
//        for (User user : foundUsers) {
//            if (!oldMail.getUsers().contains(user)) {
//                oldMail.getUsers().add(user);
//            }
//        }

        oldMail.setMailRecipient(mail.getMailRecipient());
        oldMail.setContent(mail.getContent());
        if(mail.getStatus()!=null)
            oldMail.setStatus(mail.getStatus());
        oldMail.setTitle(mail.getTitle());
        oldMail.setDateSend(mail.getDateSend());
        oldMail.setUpdatedAt(DateUtils.getCurrentDay());
        oldMail.setUsers(foundUsers);
        mailRepo.save(oldMail);
    }

    @Override
    public void deleteMail(int id) {
        Optional<Mail> oldObj = mailRepo.findById(id);
        if (oldObj.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + id);
        }
        mailRepo.deleteById(id);
    }

    @Override
    public Page<MailForm> findAllMailPaginable(Pageable pageable) {
        Page<Mail> mailList = mailRepo.findAll(pageable);
        List<MailForm> mailFormList = mailList
                .stream()
                .map(this::convertToMailDto)
                .collect(Collectors.toList());

        return new PageImpl<>(mailFormList, pageable, mailList.getTotalElements());
    }

    @Override
    public Page<MailForm> findByTitleContainingIgnoreCase(String keyword, Pageable pageable) {
        Page<Mail> mailList = mailRepo.findByTitleContainingIgnoreCase(keyword, pageable);
        List<MailForm> mailFormList = mailList
                .stream()
                .map(this::convertToMailDto)
                .collect(Collectors.toList());

        return new PageImpl<>(mailFormList, pageable, mailList.getTotalElements());
    }


    private MailForm convertToMailDto(Mail mail) {
        MailForm mailForm = new MailForm();
        mailForm.setMailId(mail.getMailId());
//        mailForm.setMailRecipient(mailRepo.findRecipentByMailId(mail.getMailId()));
        mailForm.setMailRecipient(mail.getMailRecipient());

        mailForm.setDateSend(mail.getDateSend());
        mailForm.setTimeSend(mail.getTimeSend());
        mailForm.setStatus(mail.getStatus());
        mailForm.setContent(mail.getContent());
        mailForm.setTitle(mail.getTitle());
        mailForm.setCreatedAt(mail.getCreatedAt());
        mailForm.setUpdatedAt(mail.getUpdatedAt());
        // Assuming you have a UserDto and convert User to UserDto here
        // mailDto.setUser(convertToUserDto(mail.getUser()));
        return mailForm;
    }

    public static Mail convertToMail(MailForm mailForm) {
        if (mailForm == null) {
            return null;
        }

        return com.example.demo.entity.Mail.builder()
                .mailId(mailForm.getMailId())
                .mailRecipient(mailForm.getMailRecipient())
                .dateSend(mailForm.getDateSend())
                .timeSend(mailForm.getTimeSend())
                .status(mailForm.getStatus())
                .content(mailForm.getContent())
                .title(mailForm.getTitle())
                .createdAt(mailForm.getCreatedAt())
                .updatedAt(mailForm.getUpdatedAt())
                .users(mailForm.getUsers()) // Ensure this is a correct list of User entities
                .build();
    }

}
