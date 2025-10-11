package modeles;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

public class Trajet {

    // Attributs
    private Integer idLivreur;
    private List<Site> sites;
    private Float dureeTrajet;
    private LocalTime heureDebut;
    private LocalTime heureFin;


    // Constructeur
    public Trajet(Integer id) {
        this.idLivreur = id;
        this.sites = new ArrayList<>();
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }

    // Getters et Setters

    public Integer getIdLivreur() {
        return idLivreur;
    }

    public void setIdLivreur(Integer id){
        this.idLivreur = id;
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
}