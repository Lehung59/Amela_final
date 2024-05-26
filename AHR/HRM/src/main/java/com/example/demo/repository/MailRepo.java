package com.example.demo.repository;

import com.example.demo.entity.Mail;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MailRepo extends JpaRepository<Mail, Integer> {

    @Query(" select distinct m from Mail m join m.users u where " +
            "m.title LIKE %:keyword% or " +
            "CONCAT(u.firstName, ' ', u.lastName) LIKE %:keyword%")
    Page<Mail> findByTitleContainingIgnoreCase(@Param("keyword") String keyword, Pageable paging);

    @Query("select group_concat(' ',u.email) from User u join u.mails m where m.mailId = :mailId")
    String findRecipentByMailId(int mailId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users_mails WHERE mail_id = :mailId", nativeQuery = true)
    void deleteAllUsersByMailId(@Param("mailId") int mailId);


    @Modifying
    @Transactional
    @Query("DELETE FROM Mail m WHERE m.mailId = :mailId")
    void deleteByMailId(@Param("mailId") int mailId);
}
