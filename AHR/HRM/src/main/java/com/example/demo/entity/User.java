package com.example.demo.entity;

import com.example.demo.rule.ValidEmail;
import com.example.demo.rule.ValidPassword;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
//    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @ValidEmail(message = "Invalid syntax")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Column(nullable = false)
    @ValidPassword
    private String password;

    @NotEmpty(message = "Firstname cannot be empty")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Lastname cannot be empty")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_actived")
    private Boolean isActived;


    @Column(name = "is_male")
    private boolean isMale;

    @Temporal(TemporalType.DATE) // Ensure the date is handled as a SQL DATE
    @DateTimeFormat(pattern = "yyyy-MM-dd") // For MVC binding, not for JPA/Hibernate
    private Date birthday;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Token> tokens;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_mails",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mail_id", referencedColumnName = "mailid")
    )
    @JsonManagedReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Mail> mails;

}
