package com.example.demo.service.implement;

import com.example.demo.entity.MailStatus;
import com.example.demo.entity.User;
import com.example.demo.form.MailForm;
import com.example.demo.exception.MailNotFoundException;
import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.MailUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements com.example.demo.service.MailService {

    private final MailRepo mailRepo;
    private final UserRepo userRepo;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final MailUtils mailUtils;

    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new HashMap<>();


    @PostConstruct
    public void scanMailToSend() {
        try {
            List<Mail> mails = mailRepo.findAll();
            for (Mail mail : mails) {
                if (mail.getStatus() == MailStatus.PENDING) {
                    LocalDateTime timeSend = LocalDateTime.of(mail.getDateSend(), mail.getTimeSend());

                    if (timeSend.isAfter(LocalDateTime.now())) {
                        scheduleEmail(mail.getMailId());
                    }
                    mail.setStatus(MailStatus.FAILED);
                    mailRepo.save(mail);
                }
            }
            printTaskPool();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
        return convertToMailDto(mail.get());
    }

    @Override
    @Transactional
    public void saveMail(MailForm mailForm) {

        Mail mail = convertToMail(mailForm);
        Mail newObj = new Mail();
        newObj.setMailRecipient(mail.getMailRecipient());
        newObj.setContent(mail.getContent());
        newObj.setCreatedAt(DateUtils.getCurrentDay());
        newObj.setUpdatedAt(DateUtils.getCurrentDay());
        newObj.setDateSend(mail.getDateSend());
        newObj.setStatus(MailStatus.PENDING);
        newObj.setTimeSend(mail.getTimeSend());
        newObj.setTitle(mail.getTitle());

        String listMails = mailForm.getMailRecipient().replaceAll("\\s+", "");
        List<String> emailList = Arrays.asList(listMails.split(","));
        List<User> foundUsers = new ArrayList<>();
        for (String email : emailList) {
            Optional<User> userOptional = userRepo.findByEmail(email);
            if (userOptional.isEmpty()) throw new MailNotFoundException("Mail not found:" + email);
            userOptional.ifPresent(foundUsers::add);
        }
//        newObj.getUsers().addAll(foundUsers);
        newObj.setUsers(foundUsers);


        newObj = mailRepo.save(newObj);

//        LocalDateTime dateTime = LocalDateTime.of(mail.getDateSend(),mail.getTimeSend());
////        scheduleEmail(dateTime);
//        String recipientAddress = mail.getMailRecipient();
//        String subject = mail.getTitle();
//        String message = mail.getContent();
        int id = newObj.getMailId();
        scheduleEmail(newObj.getMailId());

    }

    public void scheduleEmail(int id) {
//        Instant instant = DateUtils.getCurrentDay().toInstant().plusSeconds(10);
//        Date sendDate = Date.from(instant);
        Mail mail = mailRepo.findById(id).get();
//        LocalDateTime dateTime = LocalDateTime.of(mail.getDateSend(),mail.getTimeSend());
        ZonedDateTime zonedDateTime = ZonedDateTime.of(mail.getDateSend(), mail.getTimeSend(), ZoneId.systemDefault());


        String recipientAddress = mail.getMailRecipient();
        String subject = mail.getTitle();
        String message = mail.getContent();
//        taskScheduler.schedule(() -> sendEmail(recipientAddress,subject,message,id), zonedDateTime.toInstant());
        ScheduledFuture<?> future = taskScheduler.schedule(() -> sendEmail(recipientAddress, subject, message, id), zonedDateTime.toInstant());
        scheduledTasks.put(id, future);
        printTaskPool();
    }

    @Transactional
    public void sendEmail(String recipientAddress, String subject, String message, int id) {
        mailUtils.sendContent(recipientAddress, subject, message);
        Mail mail = mailRepo.findById(id).get();
        mail.setStatus(MailStatus.SENT);
        mailRepo.save(mail);
        // Hàm giả lập gửi email
//        System.out.println("Sending email at " + DateUtils.getCurrentDay());
        // Thêm logic gửi email tại đây
    }


    @Override
    @Transactional
    public void updateMail(MailForm mailForm) {
        Mail mail = convertToMail(mailForm);
        int id = mailForm.getMailId();
        Optional<Mail> oldObj = mailRepo.findById(id);
        cancelScheduledEmail(id);
        mailRepo.deleteAllUsersByMailId(mail.getMailId());

        String listMails = mailForm.getMailRecipient().replaceAll("\\s+", "");
        List<String> emailList = Arrays.asList(listMails.split(","));
        List<User> foundUsers = new ArrayList<>();
        for (String email : emailList) {
            Optional<User> userOptional = userRepo.findByEmail(email);
            if (userOptional.isEmpty()) throw new MailNotFoundException("Mail not found:" + email);
            userOptional.ifPresent(foundUsers::add);
        }

        if (oldObj.isEmpty()) {
            throw new MailNotFoundException("Mail not found with ID: " + mail.getMailId());
        }
        Mail oldMail = oldObj.get();
        oldMail.getUsers().clear();

        oldMail.getUsers().addAll(foundUsers);


        oldMail.setMailRecipient(mail.getMailRecipient());
        oldMail.setContent(mail.getContent());

        ZonedDateTime zonedDateTime = ZonedDateTime.of(mail.getDateSend(), mail.getTimeSend(), ZoneId.systemDefault());

        ScheduledFuture<?> future = taskScheduler.schedule(() -> sendEmail(oldMail.getMailRecipient(), oldMail.getTitle(), oldMail.getContent(), id), zonedDateTime.toInstant());
        scheduledTasks.put(id, future);
        printTaskPool();
        oldMail.setTimeSend(mail.getTimeSend());
        oldMail.setStatus(MailStatus.PENDING);
        oldMail.setTitle(mail.getTitle());
        oldMail.setDateSend(mail.getDateSend());
        oldMail.setUpdatedAt(DateUtils.getCurrentDay());
        oldMail.setUsers(foundUsers);
        mailRepo.save(oldMail);

        printTaskPool();
    }

    @Override
    @Transactional
    public void deleteMail(int id) {
        Mail mail = mailRepo.findById(id)
                .orElseThrow(() -> new MailNotFoundException("Mail not found with ID: " + id));
        List<User> users = new ArrayList<>(mail.getUsers()); // Make a copy to avoid ConcurrentModificationException
        for (User user : users) {
            user.getMails().remove(mail);
            userRepo.save(user); // Save each user after removing the mail
        }
        mailRepo.deleteAllUsersByMailId(id);
        mailRepo.deleteByMailId(id);

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

    @Override
    public Optional<Mail> checkExsist(Integer id) {
        return mailRepo.findById(id);
    }

    @Override
    @Transactional
    public void draftNewMail(MailForm mailForm) {
        Mail mail = convertToMail(mailForm);
        mail.setCreatedAt(DateUtils.getCurrentDay());
        mail.setUpdatedAt(DateUtils.getCurrentDay());
        mailRepo.save(mail);

        printTaskPool();

    }

    @Override
    @Transactional
    public void draftExistMail(MailForm mailForm) {
        cancelScheduledEmail(mailForm.getMailId());
        System.out.println(mailForm.getMailId());
        Mail oldMail = mailRepo.findById(mailForm.getMailId()).get();
        oldMail.setStatus(MailStatus.DRAFT);

        oldMail.setUpdatedAt(DateUtils.getCurrentDay());
        oldMail.setDateSend(mailForm.getDateSend());
        oldMail.setTitle(mailForm.getTitle());
        oldMail.setContent(mailForm.getContent());
        oldMail.setMailRecipient(mailForm.getMailRecipient());
        oldMail.setTimeSend(mailForm.getTimeSend());

        if (!mailForm.getMailRecipient().isEmpty()) {
            String listMails = mailForm.getMailRecipient().replaceAll("\\s+", "");
            String[] emailList = listMails.split(",");
            List<User> foundUsers = new ArrayList<>();
            for (String email : emailList) {
                Optional<User> userOptional = userRepo.findByEmail(email);
                if (userOptional.isEmpty()) throw new MailNotFoundException("Mail not found:" + email);
                userOptional.ifPresent(foundUsers::add);
            }
            oldMail.getUsers().clear();
            oldMail.getUsers().addAll(foundUsers);
        }




        try {
            Mail oll = mailRepo.save(oldMail);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Mail newMail = convertToMail(mailForm);

        printTaskPool();


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

    public void printTaskPool() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) taskScheduler.getScheduledExecutor();
        System.out.println("Thông tin về các nhiệm vụ lên lịch:");
        for (Map.Entry<Integer, ScheduledFuture<?>> entry : scheduledTasks.entrySet()) {
            int id = entry.getKey();
            ScheduledFuture<?> future = entry.getValue();
            long delay = future.getDelay(TimeUnit.MINUTES); // Lấy thời gian còn lại cho đến khi nhiệm vụ được chạy (tính bằng giây)

            System.out.println("ID của email: " + id + ", Thời gian chờ: " + delay + " phút");
        }
        // Nếu bạn muốn in ra thông tin chi tiết về mỗi task, bạn có thể lặp qua hàng đợi và in ra thông tin của từng task.
    }

    public void cancelScheduledEmail(int id) {
        ScheduledFuture<?> future = scheduledTasks.get(id);
        if (future != null) {
            future.cancel(true); // Hủy bỏ nhiệm vụ
            scheduledTasks.remove(id); // Xóa ánh xạ trong Map
        }
    }

}
