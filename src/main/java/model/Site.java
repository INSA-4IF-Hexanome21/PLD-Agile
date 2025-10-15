package model;

import java.time.LocalTime;

public abstract class Site {

    //Attributs
    protected long id;
    protected Float lat; 
    protected Float lng; 
    protected LocalTime depart_heure = null;
    protected LocalTime arrivee_heure = null;

    // Constructeur minimal
    public Site(long id) {
        this.id = id;
        this.lat = null;
        this.lng = null;
    }

    // Constructeur complet
    public Site(long id, Float lat, Float lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    //Getters / Setters
    public long getId() {
        return id;
    }

    public Float getLat() {
        return lat;
    }
    
    public Float getLng() {
        return lng;
    }
    
    public LocalTime getDepartHeure() {
        return depart_heure;
    }
    
    public LocalTime getArriveeHeure() {
        return arrivee_heure;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }    

    public void setDepartHeure(LocalTime depart_heure) {
        this.depart_heure = depart_heure;
    }

    public void setArriveeHeure(LocalTime arrivee_heure) {
        this.arrivee_heure = arrivee_heure;
    }

    /**
     * Retourne le type de site ("entrepot", "livraison", "collecte", etc.)
     */
    public abstract String getTypeSite();
}
