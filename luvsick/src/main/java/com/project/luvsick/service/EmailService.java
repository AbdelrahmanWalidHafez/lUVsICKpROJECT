package com.project.luvsick.service;

import com.project.luvsick.model.Order;
import com.project.luvsick.model.OrderStatus;

import java.util.UUID;
/**
 * Service interface for sending various notification emails.
 * @author Abdelrhman Walid Hafez
 */
public interface EmailService {
 /**
  * Sends a notification email about a new arrival product identified by the given ID.
  *
  * @param id the UUID of the new arrival product
  */
 void  sendNewArrivalEmail(UUID id);
 /**
  * Sends an email confirming that an order has been received to the specified email address.
  *
  * @param email the recipient's email address
  * @param order {@link Order} the customer's order details
  */
 void sendOrderReceivedEmail(String email, Order order);
 /**
  * Sends an email to notify the recipient about an update to their order status.
  *
  * @param email       the recipient's email address
  * @param orderStatus the new status of the order
  */
 void sendNewOrderStatusEmail(String email, OrderStatus orderStatus);
}
