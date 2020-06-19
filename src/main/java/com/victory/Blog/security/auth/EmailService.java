package com.victory.blog.security.auth;

import com.victory.blog.base.user.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("emailService")
public class EmailService extends JavaMailSenderImpl {

    @Inject
    private JavaMailSender mailSender;

    public void sendMail(String to, String msg) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("info@victorysblog.com");
        message.setTo(to);
        message.setSubject("Confirm your account");
        message.setText(msg);
        mailSender.send(message);
    }

    public void mailForReset(User user, String token) {
        sendMail(user.getEmail(), "Dear " + user.getFirstname()
                + ", here is your link for password changing." + '\n'
                + "Follow this link: " + "http://localhost:8080/auth/reset/" + token + '\n'
                + "If it was not you, please, ignore this message.");
    }

    public void mailForRegistration(User user, String token) {
        sendMail(user.getEmail(), "Dear " + user.getFirstname()
                + ", please, confirm your email. This link is valid for 24 hours." + '\n'
                + "Follow this link: " + "http://localhost:8080/auth/confirm/" + token);
    }

}
