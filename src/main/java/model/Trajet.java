package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

public class Trajet {

    // Attributs
    private Livreur livreur;
    private List<Site> sites;
<<<<<<< HEAD
=======
    private List<Site> nonAccessibles;
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
    private List<Troncon> troncons;
    private Float dureeTrajet;
    private LocalTime heureDebut;
    private LocalTime heureFin;


<<<<<<< HEAD
    // Constructeur
=======
    // Constructeur complet
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
    public Trajet(Livreur livreur) {
        this.livreur = livreur;
        this.sites = new ArrayList<>();
        this.troncons = new ArrayList<>();
<<<<<<< HEAD
=======
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
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }

    // Getters et Setters

    public Livreur getLivreur() {
        return this.livreur;
    }

<<<<<<< HEAD
    public void setIdLivreur(Livreur new_livreur){
=======
    public void setLivreur(Livreur new_livreur){
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
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

<<<<<<< HEAD
=======
    public List<Site> getSitesNonAccessibles(){
        return nonAccessibles;
    }

>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
    public List<Troncon> getTroncons() {
        return troncons;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }
}