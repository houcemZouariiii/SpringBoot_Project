package com.iit.projetjee.service;

import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.FormateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FormateurService {

    private final FormateurRepository formateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FormateurService(FormateurRepository formateurRepository, PasswordEncoder passwordEncoder) {
        this.formateurRepository = formateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Créer un formateur
    public Formateur createFormateur(Formateur formateur) {
        if (formateurRepository.existsByEmail(formateur.getEmail())) {
            throw new IllegalArgumentException("Un formateur avec cet email existe déjà");
        }
        if (formateur.getUsername() != null && formateurRepository.existsByUsername(formateur.getUsername())) {
            throw new IllegalArgumentException("Un formateur avec ce nom d'utilisateur existe déjà");
        }
        // Vérifier que le username est fourni
        if (formateur.getUsername() == null || formateur.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        // Vérifier que le mot de passe est fourni et encoder
        if (formateur.getPassword() == null || formateur.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        // Encoder le mot de passe
        formateur.setPassword(passwordEncoder.encode(formateur.getPassword().trim()));
        return formateurRepository.save(formateur);
    }

    // Obtenir tous les formateurs
    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }

    // Obtenir un formateur par ID
    public Formateur getFormateurById(Long id) {
        return formateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé avec l'ID : " + id));
    }

    // Obtenir un formateur par username
    public Formateur getFormateurByUsername(String username) {
        return formateurRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé avec le username : " + username));
    }

    // Mettre à jour un formateur
    public Formateur updateFormateur(Long id, Formateur formateurDetails) {
        Formateur formateur = getFormateurById(id);
        
        // Vérifier si l'email est modifié et s'il existe déjà
        if (!formateur.getEmail().equals(formateurDetails.getEmail()) && 
            formateurRepository.existsByEmail(formateurDetails.getEmail())) {
            throw new IllegalArgumentException("Un formateur avec cet email existe déjà");
        }
        
        formateur.setNom(formateurDetails.getNom());
        formateur.setPrenom(formateurDetails.getPrenom());
        formateur.setEmail(formateurDetails.getEmail());
        formateur.setTelephone(formateurDetails.getTelephone());
        formateur.setSpecialite(formateurDetails.getSpecialite());
        formateur.setAnneesExperience(formateurDetails.getAnneesExperience());
        
        // Mettre à jour le username si fourni
        if (formateurDetails.getUsername() != null && !formateurDetails.getUsername().isEmpty()) {
            if (!formateur.getUsername().equals(formateurDetails.getUsername()) && 
                formateurRepository.existsByUsername(formateurDetails.getUsername())) {
                throw new IllegalArgumentException("Un formateur avec ce nom d'utilisateur existe déjà");
            }
            formateur.setUsername(formateurDetails.getUsername());
        }
        
        // Mettre à jour le mot de passe si fourni
        if (formateurDetails.getPassword() != null && !formateurDetails.getPassword().trim().isEmpty()) {
            formateur.setPassword(passwordEncoder.encode(formateurDetails.getPassword().trim()));
        }
        
        return formateurRepository.save(formateur);
    }

    // Supprimer un formateur
    public void deleteFormateur(Long id) {
        Formateur formateur = getFormateurById(id);
        // Vérifier si le formateur a des cours assignés
        if (!formateur.getCours().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer le formateur : il a des cours assignés");
        }
        formateurRepository.delete(formateur);
    }

    // Rechercher des formateurs
    public List<Formateur> searchFormateurs(String search) {
        return formateurRepository.findByNomOrPrenomContaining(search);
    }
}

