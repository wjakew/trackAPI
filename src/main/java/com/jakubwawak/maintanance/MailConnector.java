package com.jakubwawak.maintanance;

import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sound.midi.Track;
import java.sql.SQLException;
import java.util.Properties;

public class MailConnector{

    private JavaMailSender emailSender;
    public boolean error;
    /**
     * Constructor
     */
    public MailConnector(){
        emailSender = getJavaMailSender();
        error = false;
    }

    /**
     * Function for sending email tu users
     * @param to
     * @param subject
     * @param text
     */
    public void send(String to, String subject, String text) throws SQLException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("main.tes.instruments@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        }catch(Exception e){
            TrackApiApplication.database.log("Failed to send message ("+e.toString()+")","MAIL-SEND-FAILED");
            error = true;
        }
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
