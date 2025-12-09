package com.iit.projetjee.service;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.EtudiantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");
    }

    @Test
    void testCreateEtudiant() {
        when(etudiantRepository.existsByEmail(etudiant.getEmail())).thenReturn(false);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        Etudiant result = etudiantService.createEtudiant(etudiant);

        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testCreateEtudiantWithExistingEmail() {
        when(etudiantRepository.existsByEmail(etudiant.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            etudiantService.createEtudiant(etudiant);
        });
    }

    @Test
    void testGetAllEtudiants() {
        List<Etudiant> etudiants = Arrays.asList(etudiant);
        when(etudiantRepository.findAll()).thenReturn(etudiants);

        List<Etudiant> result = etudiantService.getAllEtudiants();

        assertEquals(1, result.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testGetEtudiantById() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        Etudiant result = etudiantService.getEtudiantById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetEtudiantByIdNotFound() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            etudiantService.getEtudiantById(1L);
        });
    }

    @Test
    void testUpdateEtudiant() {
        Etudiant updatedEtudiant = new Etudiant();
        updatedEtudiant.setNom("Martin");
        updatedEtudiant.setPrenom("Pierre");
        updatedEtudiant.setEmail("pierre.martin@example.com");

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.existsByEmail(updatedEtudiant.getEmail())).thenReturn(false);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(updatedEtudiant);

        Etudiant result = etudiantService.updateEtudiant(1L, updatedEtudiant);

        assertNotNull(result);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
    }

    @Test
    void testDeleteEtudiant() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        doNothing().when(etudiantRepository).delete(etudiant);

        etudiantService.deleteEtudiant(1L);

        verify(etudiantRepository, times(1)).delete(etudiant);
    }
}

