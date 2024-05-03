package com.example.demo.repository;

import com.example.demo.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepo extends JpaRepository<Mail, Integer> {
}
