package com.project.luvsick.service;

import com.project.luvsick.model.OrderStatus;

import java.util.UUID;

public interface EmailService {
 void  sendNewArrivalEmail(UUID id);

 void sendOrderReceivedEmail(String email);

 void sendNewOrderStatusEmail(String email, OrderStatus orderStatus);
}
