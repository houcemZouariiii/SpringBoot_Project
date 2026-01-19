package com.iit.projetjee.service;

public interface INotificationEmailService {
    void sendNoteNotification(String to, String etudiantNom, String coursTitre, Double note);
    void sendNoteNotification(String to, String etudiantNom, String coursTitre, Double note, 
                             String typeEvaluation, String commentaire, boolean isUpdate);
}
