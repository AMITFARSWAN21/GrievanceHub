package com.authify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome on our platform");
        message.setText("Hello " + name + ",\n\nThanks for registering with us!\n\nRegards,\nAuthify Team");
        mailSender.send(message); // <-- Important: actually send the email
    }

    public void sendResetOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password resend Otp");
        message.setText("Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail,String otp)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Account Verification");
        message.setText("Your OTP code is: " + otp + "\n Verify your Account using this Otp.");
        mailSender.send(message);
    }

    public void complaintIssueResolved(String toEmail,String complaintTitle)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Complaint Issue Resolved");
        message.setText("Dear user,\n\nYour complaint titled \"" + complaintTitle + "\" has been marked as RESOLVED. Thank you for reporting!\n\nRegards,\nCommunity Support Team");
        mailSender.send(message);
    }


}
