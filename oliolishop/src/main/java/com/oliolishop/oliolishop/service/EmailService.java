package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.constant.MessageConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailService {
    @NonFinal
    @Value("${spring.mail.username}")
    protected String EMAIL;
    JavaMailSender mailSender;


    public void sendOtp(String to, String otp) {

        try {
            String html = loadHtmlTemplate("otp_mail.html");
            html = html.replace("${otp}", otp);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL);
            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText(html, true);


            mailSender.send(message);
        }
        catch (MessagingException e){
            System.err.println("Lỗi gửi email OTP (MessagingException): " + e.getMessage());
        }
    }



    public String loadHtmlTemplate(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {

            throw new RuntimeException("Cannot load template: " + fileName);
        }
    }

    public void sendAccountEmployee(String to, String name, String username){
        try {
            String html = loadHtmlTemplate("account_employee.html");
            html = html.replace("${fullName}", name);
            html = html.replace("${username}",username);
            html = html.replace("${password}", MessageConstants.DEFAULT_PASSWORD);
            html = html.replace("${supportEmail}",EMAIL);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL);
            helper.setTo(to);
            helper.setSubject("Your Account Olioli Fashion");
            helper.setText(html, true);

            mailSender.send(message);
        }
        catch (MessagingException e){
            System.err.println("Lỗi gửi email (MessagingException): " + e.getMessage());
        }
    }

}
