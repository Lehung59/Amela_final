package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail")
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mailid")
    private int mailId;

    @Column(name = "mailrecipient", nullable = false)
    private String mailRecipient;

    @Column(name = "date_send")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateSend;

    @Column(name = "time_send")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime timeSend;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MailStatus status;

    @Column(name = "content")
    private String content;

    @Column(name = "title")
    private String title;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Getters and setters

//    public enum MailStatus {
//        SENT, FAILED, PENDING, DRAFT
//    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "mails", cascade = CascadeType.ALL)
    @JsonBackReference
    List<User> users;

    public void setUsers(List<User> users) {
        this.users = users;
        for (User user : users) {
            user.getMails().add(this);
        }
    }
}
