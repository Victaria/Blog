package com.victory.Blog.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService extends JavaMailSenderImpl {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String msg) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("info@victorysblog.com");
        message.setTo(to);
        message.setSubject("Confirm your account");
        message.setText(msg);
        mailSender.send(message);
    }

}
