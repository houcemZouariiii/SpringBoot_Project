package com.iit.projetjee.service;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.EtudiantRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    private final EtudiantRepository etudiantRepository;
    private final NoteService noteService;

    @Autowired
    public ReportService(EtudiantRepository etudiantRepository, NoteService noteService) {
        this.etudiantRepository = etudiantRepository;
        this.noteService = noteService;
    }

    /**
     * Génère un relevé PDF simple pour un étudiant (notes + moyenne générale).
     */
    public byte[] generateReleveEtudiant(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + etudiantId));

        List<Note> notes = noteService.getNotesByEtudiant(etudiantId);
        double moyenne = noteService.calculMoyenneGeneraleEtudiant(etudiantId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Relevé de notes"));
            document.add(new Paragraph("Étudiant : " + etudiant.getNomComplet() + " (" + etudiant.getEmail() + ")"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Cours");
            table.addCell("Type");
            table.addCell("Note");

            for (Note note : notes) {
                table.addCell(note.getCours() != null ? note.getCours().getTitre() : "-");
                table.addCell(note.getTypeEvaluation() != null ? note.getTypeEvaluation() : "-");
                table.addCell(note.getValeur() != null ? String.format("%.2f", note.getValeur()) : "-");
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Moyenne générale : " + String.format("%.2f", moyenne)));

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        }
    }
}

