package model;

import java.time.LocalTime;

public abstract class Site extends Noeud  {

    // Attributs
    protected LocalTime depart_heure = null;
    protected LocalTime arrivee_heure = null;
    protected Integer numPassage = null;

    // Constructeur minimal
    public Site(long id) {
        super(id, null, null);
    }

    // Constructeur complet
    public Site(long id, Float lat, Float lng) {
        super(id, lat, lng);
    }

    // Getters / Setters
    public LocalTime getDepartHeure() {
        return depart_heure;
    }
    
    public LocalTime getArriveeHeure() {
        return arrivee_heure;
    }

    public Integer getNumPassage() {
        return numPassage;
    }
   
    public void setDepartHeure(LocalTime depart_heure) {
        this.depart_heure = depart_heure;
    }

    public void setArriveeHeure(LocalTime arrivee_heure) {
        this.arrivee_heure = arrivee_heure;
    }

    public void setNumPassage(Integer numPassage) {
        this.numPassage = numPassage;
    }

    

    /**
     * Retourne le type de site ("entrepot", "livraison", "collecte", etc.)
     */
    public abstract String getTypeSite();
}
