package com.iit.projetjee.service;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EtudiantService(EtudiantRepository etudiantRepository, PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Créer un étudiant
    public Etudiant createEtudiant(Etudiant etudiant) {
        if (etudiantRepository.existsByEmail(etudiant.getEmail())) {
            throw new IllegalArgumentException("Un étudiant avec cet email existe déjà");
        }
        if (etudiant.getUsername() != null && etudiantRepository.existsByUsername(etudiant.getUsername())) {
            throw new IllegalArgumentException("Un étudiant avec ce nom d'utilisateur existe déjà");
        }
        // Encoder le mot de passe si fourni
        if (etudiant.getPassword() != null && !etudiant.getPassword().isEmpty()) {
            etudiant.setPassword(passwordEncoder.encode(etudiant.getPassword()));
        }
        return etudiantRepository.save(etudiant);
    }

    // Obtenir tous les étudiants
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    // Obtenir un étudiant par username
    public Etudiant getEtudiantByUsername(String username) {
        return etudiantRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec le username : " + username));
    }

    // Obtenir un étudiant par ID
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + id));
    }

    // Mettre à jour un étudiant
    public Etudiant updateEtudiant(Long id, Etudiant etudiantDetails) {
        Etudiant etudiant = getEtudiantById(id);
        
        // Vérifier si l'email est modifié et s'il existe déjà
        if (!etudiant.getEmail().equals(etudiantDetails.getEmail()) && 
            etudiantRepository.existsByEmail(etudiantDetails.getEmail())) {
            throw new IllegalArgumentException("Un étudiant avec cet email existe déjà");
        }
        
        // Vérifier si le username est modifié et s'il existe déjà
        if (etudiantDetails.getUsername() != null && 
            !etudiantDetails.getUsername().equals(etudiant.getUsername()) &&
            etudiantRepository.existsByUsername(etudiantDetails.getUsername())) {
            throw new IllegalArgumentException("Un étudiant avec ce nom d'utilisateur existe déjà");
        }
        
        etudiant.setNom(etudiantDetails.getNom());
        etudiant.setPrenom(etudiantDetails.getPrenom());
        etudiant.setEmail(etudiantDetails.getEmail());
        etudiant.setTelephone(etudiantDetails.getTelephone());
        etudiant.setAdresse(etudiantDetails.getAdresse());
        etudiant.setSection(etudiantDetails.getSection());
        
        // Mettre à jour le username si fourni
        if (etudiantDetails.getUsername() != null) {
            etudiant.setUsername(etudiantDetails.getUsername());
        }
        
        // Mettre à jour le mot de passe si fourni (et différent de l'actuel)
        if (etudiantDetails.getPassword() != null && !etudiantDetails.getPassword().isEmpty()) {
            etudiant.setPassword(passwordEncoder.encode(etudiantDetails.getPassword()));
        }
        
        return etudiantRepository.save(etudiant);
    }

    // Supprimer un étudiant
    public void deleteEtudiant(Long id) {
        Etudiant etudiant = getEtudiantById(id);
        etudiantRepository.delete(etudiant);
    }

    // Rechercher des étudiants
    public List<Etudiant> searchEtudiants(String search) {
        return etudiantRepository.findByNomOrPrenomContaining(search);
    }

    // Vérifier si un email existe
    public boolean emailExists(String email) {
        return etudiantRepository.existsByEmail(email);
    }
}

