package model;

import java.util.Objects;

public class Troncon {
    // Méthodes 
    private String nomRue;
    private float longueur;
    private Noeud origine;
    private Noeud destination;

    // Constructeur
    public Troncon(String nomRue, float longueur, Noeud origine, Noeud destination) {
        this.nomRue = nomRue;
        this.longueur = longueur;
        this.origine = origine;
        this.destination = destination;
    }

    // Getters
    public String getNomRue() {
        return nomRue;
    }

    public float getLongueur() {
        return longueur;
    }

    public Noeud getOrigine() {
        return origine;
    }

    public Noeud getDestination() {
        return destination;
    }

    // Méthodes 
    @Override
    public String toString() {
        return "Troncon{" +
                "nomRue='" + nomRue + '\'' +
                ", longueur=" + longueur +
                ", origine=" + (origine != null ? origine.getId() : "null ") +
                ", destination=" + (destination != null ? destination.getId() : "null ") +
                '}';
    }

    //Méthode éventuel pour éviter les doublons.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Troncon)) return false;

        Troncon other = (Troncon) obj;

        return Objects.equals(this.nomRue, other.nomRue) &&
               Objects.equals(this.origine, other.origine) &&
               Objects.equals(this.destination, other.destination);
    }

    //Méthode éventuel pour les tables de haschage
    @Override
    public int hashCode() {
        return Objects.hash(nomRue, origine, destination);
    }
}
