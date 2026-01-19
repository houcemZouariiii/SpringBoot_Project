package com.iit.projetjee.service;

import java.util.List;
import java.util.Map;

public interface IAdminEmailService {
    void sendEmailFromAdmin(String to, String recipientName, String subject, String message);
    void sendBulkEmailFromAdmin(List<String> recipients, Map<String, String> recipientNames, 
                               String subject, String message);
}
