package com.jakubwawak.maintanance;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class MailConnector{

    private JavaMailSender emailSender;

    /**
     * Constructor
     */
    public MailConnector(){
        emailSender = getJavaMailSender();
    }

    /**
     * Function for sending email tu users
     * @param to
     * @param subject
     * @param text
     */
    public void send(String to, String subject, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("main.tes.instruments@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
    }

    /**
     * Function for getting object for sender
     * @return JavaMailSender
     */
    JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("main.tes.instruments@gmail.com");
        mailSender.setPassword("minidysk");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
