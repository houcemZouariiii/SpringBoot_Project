package com.iit.projetjee.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.service.EtudiantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EtudiantRestController.class)
class EtudiantRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EtudiantService etudiantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetAllEtudiants() throws Exception {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");

        List<Etudiant> etudiants = Arrays.asList(etudiant);
        when(etudiantService.getAllEtudiants()).thenReturn(etudiants);

        mockMvc.perform(get("/api/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }

    @Test
    @WithMockUser
    void testGetEtudiantById() throws Exception {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");

        when(etudiantService.getEtudiantById(1L)).thenReturn(etudiant);

        mockMvc.perform(get("/api/etudiants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    @WithMockUser
    void testCreateEtudiant() throws Exception {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");

        Etudiant savedEtudiant = new Etudiant();
        savedEtudiant.setId(1L);
        savedEtudiant.setNom("Dupont");
        savedEtudiant.setPrenom("Jean");
        savedEtudiant.setEmail("jean.dupont@example.com");

        when(etudiantService.createEtudiant(any(Etudiant.class))).thenReturn(savedEtudiant);

        mockMvc.perform(post("/api/etudiants")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(etudiant)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}

