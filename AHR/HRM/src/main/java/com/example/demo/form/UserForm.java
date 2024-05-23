package com.example.demo.form;


import com.example.demo.entity.Attendance;
import com.example.demo.entity.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserForm {

    private int id;
//    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    private String password;
    @NotEmpty(message = "Firstname cannot be empty")

    private String firstName;
    @NotEmpty(message = "Lastname cannot be empty")

    private String lastName;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private String address;

    private String avatar;

    private boolean active;

    private Role role;

    private boolean isMale;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // For MVC binding, not for JPA/Hibernate
    private Date birthday;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private MultipartFile avatarFile;

    private List<Attendance> attendances;
}
