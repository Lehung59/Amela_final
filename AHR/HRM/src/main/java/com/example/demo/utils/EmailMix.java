package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailMix {
    private String myEmail;
    private String emailPass;
    private int count = 1;

    public EmailMix(String myEmail, String emailPass16Words, int con) {
        this.myEmail = myEmail;
        this.emailPass = emailPass16Words;
    }
    public boolean sendContent(String toEmails, String headerEmail, String body) {
        // Loại bỏ các ký tự không cần thiết (ví dụ: dấu ngoặc nhọn).
        toEmails = toEmails.replaceAll("[{}]", "");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailMix.this.myEmail, EmailMix.this.emailPass);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(EmailMix.this.myEmail, "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(EmailMix.this.myEmail, false));
            msg.setSubject(headerEmail, "UTF-8");
            msg.setContent(body, "text/html; charset=UTF-8");
            msg.setSentDate(new Date());

            // Phân tích chuỗi danh sách email thành danh sách địa chỉ.
            InternetAddress[] recipients = InternetAddress.parse(toEmails, false);
            System.out.println(Arrays.toString(recipients));
            msg.setRecipients(RecipientType.TO, recipients);

            Transport.send(msg);
            return true;
        } catch (MessagingException | UnsupportedEncodingException var9) {
            System.out.println("Yêu cầu mật khẩu 16 ký tự từ xác minh 2 bước Google.");
            return false;
        }
    }
}