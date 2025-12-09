package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.FormateurRepository;
import com.iit.projetjee.repository.GroupeRepository;
import com.iit.projetjee.repository.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final CoursRepository coursRepository;
    private final FormateurRepository formateurRepository;
    private final GroupeRepository groupeRepository;

    @Autowired
    public SeanceService(SeanceRepository seanceRepository,
                         CoursRepository coursRepository,
                         FormateurRepository formateurRepository,
                         GroupeRepository groupeRepository) {
        this.seanceRepository = seanceRepository;
        this.coursRepository = coursRepository;
        this.formateurRepository = formateurRepository;
        this.groupeRepository = groupeRepository;
    }

    public Seance create(Seance seance) {
        validateAndAttach(seance);
        checkConflicts(seance, null);
        return seanceRepository.save(seance);
    }

    public Seance update(Long id, Seance data) {
        Seance existing = findById(id);
        existing.setDateDebut(data.getDateDebut());
        existing.setDateFin(data.getDateFin());
        existing.setSalle(data.getSalle());

        if (data.getCours() != null && data.getCours().getId() != null) {
            Cours cours = coursRepository.findById(data.getCours().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
            existing.setCours(cours);
        }
        if (data.getFormateur() != null && data.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(data.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));
            existing.setFormateur(formateur);
        }

        if (data.getGroupes() != null) {
            List<Groupe> groupes = loadGroupesByIds(
                    data.getGroupes().stream().map(Groupe::getId).collect(Collectors.toList()));
            existing.setGroupes(groupes);
        }

        checkConflicts(existing, existing.getId());
        return seanceRepository.save(existing);
    }

    public Seance findById(Long id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée avec l'ID : " + id));
    }

    public List<Seance> findAll() {
        return seanceRepository.findAll();
    }

    public List<Seance> findByFormateur(Long formateurId) {
        return seanceRepository.findByFormateur_Id(formateurId);
    }

    public List<Seance> findByGroupeIds(List<Long> groupeIds) {
        if (groupeIds == null || groupeIds.isEmpty()) {
            return List.of();
        }
        return seanceRepository.findByGroupeIds(groupeIds);
    }

    public void delete(Long id) {
        seanceRepository.delete(findById(id));
    }

    private void validateAndAttach(Seance seance) {
        if (seance.getCours() == null || seance.getCours().getId() == null) {
            throw new IllegalArgumentException("Le cours est obligatoire");
        }
        Cours cours = coursRepository.findById(seance.getCours().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        seance.setCours(cours);

        if (seance.getFormateur() == null || seance.getFormateur().getId() == null) {
            throw new IllegalArgumentException("Le formateur est obligatoire");
        }
        Formateur formateur = formateurRepository.findById(seance.getFormateur().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));
        seance.setFormateur(formateur);

        if (seance.getDateDebut() == null || seance.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (!seance.getDateFin().isAfter(seance.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        if (seance.getGroupes() != null && !seance.getGroupes().isEmpty()) {
            List<Long> ids = seance.getGroupes().stream()
                    .map(Groupe::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            seance.setGroupes(loadGroupesByIds(ids));
        } else {
            seance.setGroupes(new ArrayList<>());
        }
    }

    private void checkConflicts(Seance seance, Long excludeId) {
        LocalDateTime start = seance.getDateDebut();
        LocalDateTime end = seance.getDateFin();

        // Conflits formateur
        if (!seanceRepository.findConflitsFormateur(seance.getFormateur().getId(), start, end, excludeId).isEmpty()) {
            throw new IllegalStateException("Conflit d'horaires : le formateur a déjà une séance sur ce créneau");
        }

        // Conflits salle
        if (seance.getSalle() != null && !seance.getSalle().isBlank()) {
            if (!seanceRepository.findConflitsSalle(seance.getSalle(), start, end, excludeId).isEmpty()) {
                throw new IllegalStateException("Conflit d'horaires : la salle est déjà occupée sur ce créneau");
            }
        }

        // Conflits groupes
        if (seance.getGroupes() != null && !seance.getGroupes().isEmpty()) {
            List<Long> groupeIds = seance.getGroupes().stream()
                    .map(Groupe::getId)
                    .collect(Collectors.toList());
            if (!seanceRepository.findConflitsGroupes(groupeIds, start, end, excludeId).isEmpty()) {
                throw new IllegalStateException("Conflit d'horaires : un groupe est déjà planifié sur ce créneau");
            }
        }
    }

    private List<Groupe> loadGroupesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return groupeRepository.findAllById(ids);
    }
}

