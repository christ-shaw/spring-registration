package com.worldunion.springregistration.listener;

import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.event.OnRegistrationCompleteEvent;
import com.worldunion.springregistration.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent>
{


    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();

        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user,token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm.html?token=" + token;

        String message = messages.getMessage("message.regSucc", null, event.getLocale());
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " rn" + "http://localhost:8080" + confirmationUrl);
        email.setFrom("35967859@qq.com");
        email.setSentDate(new Date());
        mailSender.send(email);
    }
}