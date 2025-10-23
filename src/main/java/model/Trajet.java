package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

public class Trajet {

    // Attributs
    private Livreur livreur;
    private List<Site> sites;
    private List<Site> nonAccessibles;
    private List<Troncon> troncons;
    private Float dureeTrajet;
    private LocalTime heureDebut;
    private LocalTime heureFin;


    // Constructeur complet
    public Trajet(Livreur livreur) {
        this.livreur = livreur;
        this.sites = new ArrayList<>();
        this.troncons = new ArrayList<>();
        this.nonAccessibles = new ArrayList<>();
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }
    // Constructeur incomplet
    public Trajet() {
        this.livreur = null;
        this.sites = new ArrayList<>();
        this.troncons = new ArrayList<>();
        this.nonAccessibles = new ArrayList<>();
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }

    // Getters et Setters

    public Livreur getLivreur() {
        return this.livreur;
    }

    public void setLivreur(Livreur new_livreur){
        this.livreur = new_livreur;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public Float getdureeTrajet() {
        return dureeTrajet;
    }

    public void setdureeTrajet(float duree) {
        this.dureeTrajet = duree;
        float heure_fin = 8 + duree;
        this.heureFin = LocalTime.of((int)heure_fin,(int)((duree%1)*60));
    }

    public List<Site> getSites(){
        return sites;
    }

    public List<Site> getSitesNonAccessibles(){
        return nonAccessibles;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trajet{\n");
        
        // Informations de base
        sb.append("  Livreur: ").append(livreur != null ? livreur : "Non assigné").append("\n");
        sb.append("  Heure de début: ").append(heureDebut).append("\n");
        sb.append("  Heure de fin: ").append(heureFin != null ? heureFin : "Non calculée").append("\n");
        sb.append("  Durée du trajet: ").append(dureeTrajet != null ? String.format("%.2f heures", dureeTrajet) : "Non calculée").append("\n");
        
        // Nombre de sites
        sb.append("  Nombre de sites : ").append(sites.size()+ "\n");
        
        // Nombre de Troncons
        sb.append(troncons);
        
        sb.append("\n}");
        return sb.toString();
    }
}