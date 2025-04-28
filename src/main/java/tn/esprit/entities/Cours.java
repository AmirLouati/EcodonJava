package tn.esprit.entities;

public class Cours {
private int Id;
private String cour;
private String Description ;
private Formation formation;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCour() {
        return cour;
    }

    public void setCour(String cour) {
        this.cour = cour;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Cours(String cour, String description, Formation formation) {
        this.cour = cour;
        Description = description;
        this.formation = formation;
    }

    public Cours() {
    }
}
