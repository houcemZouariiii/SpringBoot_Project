package com.iit.projetjee.service;

import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.SessionAcademiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionAcademiqueService {

    private final SessionAcademiqueRepository sessionAcademiqueRepository;

    @Autowired
    public SessionAcademiqueService(SessionAcademiqueRepository sessionAcademiqueRepository) {
        this.sessionAcademiqueRepository = sessionAcademiqueRepository;
    }

    public SessionAcademique create(SessionAcademique session) {
        return sessionAcademiqueRepository.save(session);
    }

    public List<SessionAcademique> findAll() {
        return sessionAcademiqueRepository.findAll();
    }

    public SessionAcademique findById(Long id) {
        return sessionAcademiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session académique non trouvée avec l'ID : " + id));
    }

    public SessionAcademique update(Long id, SessionAcademique data) {
        SessionAcademique existing = findById(id);
        existing.setLibelle(data.getLibelle());
        existing.setAnnee(data.getAnnee());
        existing.setSemestre(data.getSemestre());
        existing.setActif(data.isActif());
        return sessionAcademiqueRepository.save(existing);
    }

    public void delete(Long id) {
        sessionAcademiqueRepository.delete(findById(id));
    }
}

