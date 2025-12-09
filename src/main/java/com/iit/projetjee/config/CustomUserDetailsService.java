package com.iit.projetjee.config;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.repository.EtudiantRepository;
import com.iit.projetjee.repository.FormateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(EtudiantRepository etudiantRepository,
                                   FormateurRepository formateurRepository,
                                   PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.formateurRepository = formateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Vérifier si c'est un compte système (admin)
        if (username.equals("admin")) {
            return User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin")) // Encoder le mot de passe
                    .roles("ADMIN")
                    .build();
        }

        // Chercher un formateur par username
        java.util.Optional<Formateur> formateurOpt = formateurRepository.findByUsername(username);
        if (formateurOpt.isPresent()) {
            Formateur formateur = formateurOpt.get();
            if (formateur.getPassword() != null && !formateur.getPassword().isEmpty()) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_FORMATEUR"));
                
                // Vérifier que le username n'est pas null
                if (formateur.getUsername() == null || formateur.getUsername().isEmpty()) {
                    throw new UsernameNotFoundException("Formateur trouvé mais username invalide: " + username);
                }
                
                return User.builder()
                        .username(formateur.getUsername())
                        .password(formateur.getPassword()) // Le mot de passe est déjà encodé dans la base
                        .authorities(authorities)
                        .build();
            } else {
                throw new UsernameNotFoundException("Formateur trouvé mais mot de passe non défini: " + username);
            }
        }

        // Chercher un étudiant par username
        java.util.Optional<Etudiant> etudiantOpt = etudiantRepository.findByUsername(username);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            if (etudiant.getPassword() != null && !etudiant.getPassword().isEmpty()) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
                
                return User.builder()
                        .username(etudiant.getUsername())
                        .password(etudiant.getPassword()) // Le mot de passe est déjà encodé
                        .authorities(authorities)
                        .build();
            }
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé: " + username);
    }
}

