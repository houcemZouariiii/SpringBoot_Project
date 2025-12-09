package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Etudiant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EtudiantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Test
    void testFindByEmail() {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");
        entityManager.persistAndFlush(etudiant);

        Optional<Etudiant> found = etudiantRepository.findByEmail("jean.dupont@example.com");

        assertTrue(found.isPresent());
        assertEquals("Dupont", found.get().getNom());
    }

    @Test
    void testFindByNomContainingIgnoreCase() {
        Etudiant etudiant1 = new Etudiant();
        etudiant1.setNom("Dupont");
        etudiant1.setPrenom("Jean");
        etudiant1.setEmail("jean.dupont@example.com");
        entityManager.persistAndFlush(etudiant1);

        Etudiant etudiant2 = new Etudiant();
        etudiant2.setNom("Dupont");
        etudiant2.setPrenom("Marie");
        etudiant2.setEmail("marie.dupont@example.com");
        entityManager.persistAndFlush(etudiant2);

        List<Etudiant> found = etudiantRepository.findByNomContainingIgnoreCase("dupont");

        assertEquals(2, found.size());
    }

    @Test
    void testExistsByEmail() {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean.dupont@example.com");
        entityManager.persistAndFlush(etudiant);

        boolean exists = etudiantRepository.existsByEmail("jean.dupont@example.com");

        assertTrue(exists);
    }
}

