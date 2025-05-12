package com.project.luvsick.service;

import com.project.luvsick.model.OrderStatus;

import java.util.UUID;

public interface EmailService {
public void  sendNewArrivalEmail(UUID id);

public void sendOrderReceivedEmail(String email);

   public void sendNewOrderStatusEmail(String email, OrderStatus orderStatus);
}
