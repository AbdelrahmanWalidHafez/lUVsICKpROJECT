package com.project.luvsick.service;

import com.project.luvsick.model.OrderStatus;
import com.project.luvsick.repo.CustomerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender javaMailSender;
    private final CustomerRepository customerRepository;

    @Override
    public void sendNewArrivalEmail(UUID id) {
        List<String> to =customerRepository.findAllEmails(); ;
        String subject = "new Arrival";
        String body = "<html><body>"
                + "<p>Hello, check our new product</p>"
                + "<p>Click <a href='http://localhost:8080/api/v1/"+id+"'>here</a> to visit our website.</p>"
                + "<p>Regards,<br>luvsick</p>"
                + "</body></html>";

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("walidlrahmn5@gmail.com");
            for (String email:to) {
                helper.setTo(email);
                javaMailSender.send(message);
            }
            javaMailSender.send(message);
            log.info("the emails has been sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendOrderReceivedEmail(String email) {

        String subject = "We Have Recieved your Order";
        String body = "<html><body>"
                + "<p>Hello, we will notify you on any updates on the order</p>"
                + "<p>Regards,<br>luvsick</p>"
                + "</body></html>";

       sendMessage(email,body,subject);
    }
    @Override
    public void sendNewOrderStatusEmail(String email, OrderStatus orderStatus) {
        String subject = "new Updates on your order";
        String body = "<html><body>"
                + "<p>Hello, your order is "+orderStatus+"</p>"
                + "<p>Regards,<br>luvsick</p>"
                + "</body></html>";
        sendMessage(email,body,subject);
    }
    private void  sendMessage(String email,String subject,String body){
        List<String> to =customerRepository.findAllEmails(email);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("andelrahman.walid.804@gmail.com");
            helper.setTo(email);
            javaMailSender.send(message);
            javaMailSender.send(message);
            log.info("the email has been sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (MailException e) {
            e.printStackTrace();
        }

    }
}
