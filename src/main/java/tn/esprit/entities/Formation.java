package tn.esprit.entities;

import java.sql.Date;
import java.time.LocalDateTime;

public class Formation {
    private int id;
    private Association association;
    private String titre;
    private String Description;
    private String Formateur;
    private LocalDateTime DateDebut;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFormateur() {
        return Formateur;
    }

    public void setFormateur(String formateur) {
        Formateur = formateur;
    }

    public LocalDateTime getDateDebut() {
        return DateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        DateDebut = dateDebut;
    }

    public Formation() {
    }

    public Formation(Association association, String titre, String description, String formateur, LocalDateTime dateDebut) {
        this.association = association;
        this.titre = titre;
        Description = description;
        Formateur = formateur;
        DateDebut = dateDebut;
    }

    public Formation(int id,  String titre, String description, String formateur, LocalDateTime dateDebut) {
        this.id = id;
        this.association = association;
        this.titre = titre;
        Description = description;
        Formateur = formateur;
        DateDebut = dateDebut;
    }
}
