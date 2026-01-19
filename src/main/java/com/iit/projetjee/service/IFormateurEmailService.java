package com.iit.projetjee.service;

import java.util.List;
import java.util.Map;

public interface IFormateurEmailService {
    void sendEmailFromFormateur(String to, String recipientName, String subject, String message,
                               String formateurName, String formateurEmail, String formateurSpecialite);
    void sendBulkEmailFromFormateur(List<String> recipients, Map<String, String> recipientNames,
                                   String subject, String message, String formateurName,
                                   String formateurEmail, String formateurSpecialite);
}
