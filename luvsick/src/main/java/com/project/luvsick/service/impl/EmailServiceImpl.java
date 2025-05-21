package com.project.luvsick.service.impl;

import com.project.luvsick.model.Order;
import com.project.luvsick.model.OrderStatus;
import com.project.luvsick.model.Product;
import com.project.luvsick.repo.CustomerRepository;
import com.project.luvsick.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final CustomerRepository customerRepository;
    private static final String from="luvsick2025@gmail.com";
    @Override
    @Async
    public void sendNewArrivalEmail(UUID id) {
        List<String> to =customerRepository.findAllEmails();
        String subject = "new Arrival";
        String body =
                 "Hello, check our new product"
                + "Click <a href='http://localhost:8080/api/v1/"+id+"'>here</a> to visit our website."
                + "Regards," +
                         "luvsick";

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(from);
            for (String email:to) {
                helper.setTo(email);
                javaMailSender.send(message);
            }
            javaMailSender.send(message);
            log.info("the emails has been sent");

        } catch (MessagingException | MailException e) {
            log.error(e.getMessage());
        }
    }
    @Async
    @Override
    public void sendOrderReceivedEmail(String email, Order order) {

        String subject = "We Have Received your Order";
        String productsList = order.getProducts()
                .stream()
                .map(Product::getName)
                .collect(Collectors.joining("<br>"));

        String body = "Order ID: " + order.getId() + "<br>" +
                "Order Total Price: " + order.getTotalPrice() + "<br>" +
                "Products:<br>" + productsList + "<br><br>" +
                "We will notify you on any updates on the order.<br><br>" +
                "Regards,";
       sendMessage(email,body,subject);
    }
    @Override
    public void sendNewOrderStatusEmail(String email, OrderStatus orderStatus) {
        String subject = "new Updates on your order";
        String body =
                "Hello, your order is "+orderStatus+"\n"
                + "Regards,\nluvsick";
        sendMessage(email,body,subject);
    }
    @Async
    private void  sendMessage(String email, String subject, String body){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setTo(email);
            message.setContent(body,"text/html;charset=utf-8");
            javaMailSender.send(message);
            log.info("the email has been sent");
        } catch (MessagingException | MailException e) {
            log.error(e.getMessage());
        }

    }
}
