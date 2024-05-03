package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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

    @Column(name = "mailrecipient", nullable = false, length = 255)
    private String mailRecipient;

    @Column(name = "datesend")
    @Temporal(TemporalType.DATE)
    private Date dateSend;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MailStatus status;

    @Column(name = "content")
    private String content;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Getters and setters

    public enum MailStatus {
        SENT, FAILED, PENDING
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonBackReference
//    @JoinColumn(name = "userid",referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_mail_user"))
//    private User user;

}
