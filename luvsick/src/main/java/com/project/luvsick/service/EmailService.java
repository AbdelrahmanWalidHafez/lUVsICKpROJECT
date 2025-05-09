package com.project.luvsick.service;

import java.util.UUID;

public interface EmailService {
public void  sendNewArrivalEmail(UUID id);

public void sendOrderRecievcedEmail(String email);
}
