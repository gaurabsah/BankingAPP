package com.app.bank.service.impl;

import com.app.bank.dto.EmailDetails;
import com.app.bank.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setSubject(emailDetails.getSubject());
            message.setTo(emailDetails.getRecipient());
            message.setText(emailDetails.getMessage());

            javaMailSender.send(message);
            log.info("Message sent successfully");
        } catch (Exception e){
            log.error("Error sending email {}",e.getMessage());
        }
    }
}
