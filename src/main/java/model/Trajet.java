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

    public List<Site> getSites(){
        return sites;
    }

    public List<Site> getSitesNonAccecibles(){
        return nonAccessibles;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }
}