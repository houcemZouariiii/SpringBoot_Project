package com.iit.projetjee.service;

public interface IInscriptionEmailService {
    void sendInscriptionConfirmation(String to, String etudiantNom, String coursTitre);
    void sendInscriptionValidation(String to, String etudiantNom, String coursTitre);
    void sendInscriptionNotificationFormateur(String to, String etudiantNom, String coursTitre, boolean desinscription);
}
