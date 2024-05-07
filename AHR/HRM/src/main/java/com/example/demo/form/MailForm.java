package com.example.demo.form;

import com.example.demo.entity.Mail;
import com.example.demo.entity.MailStatus;
import com.example.demo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MailForm {

    private int mailId;

    private String mailRecipient;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateSend;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime timeSend;

    private MailStatus status;

    private String content;

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private List<User> users;
}
