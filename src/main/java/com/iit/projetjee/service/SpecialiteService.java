package com.iit.projetjee.service;

import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    @Autowired
    public SpecialiteService(SpecialiteRepository specialiteRepository) {
        this.specialiteRepository = specialiteRepository;
    }

    public Specialite create(Specialite specialite) {
        if (specialiteRepository.existsByLibelleIgnoreCase(specialite.getLibelle())) {
            throw new IllegalArgumentException("Une spécialité avec ce libellé existe déjà");
        }
        return specialiteRepository.save(specialite);
    }

    public List<Specialite> findAll() {
        return specialiteRepository.findAll();
    }

    public Specialite findById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité non trouvée avec l'ID : " + id));
    }

    public Specialite update(Long id, Specialite data) {
        Specialite existing = findById(id);
        if (!existing.getLibelle().equalsIgnoreCase(data.getLibelle())
                && specialiteRepository.existsByLibelleIgnoreCase(data.getLibelle())) {
            throw new IllegalArgumentException("Une spécialité avec ce libellé existe déjà");
        }
        existing.setLibelle(data.getLibelle());
        existing.setDescription(data.getDescription());
        return specialiteRepository.save(existing);
    }

    public void delete(Long id) {
        specialiteRepository.delete(findById(id));
    }
}

