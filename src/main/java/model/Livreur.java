package model;

public class Livreur {

    // Attributs
    private long id;
    private String nom;
    private String prenom;


    // Constructeur
    public Livreur(long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et Setters

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }
}