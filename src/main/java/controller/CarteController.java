package controller;

import model.Carte;
import model.GestionnaireXML;

public class CarteController {
    private Carte carte;
    private GestionnaireXML gestionnaireXML;

    public CarteController(String cheminFichier) {
        this.carte = new Carte();
        this.gestionnaireXML = new GestionnaireXML(cheminFichier);
    }

    public void chargerCarteDepuisXML() {
        for (var n : gestionnaireXML.getNoeuds()) {
            carte.ajouterNoeud(n);
        }
        for (var t : gestionnaireXML.getTroncons()) {
            carte.ajouterTroncon(t);
        }
    }

    public Carte getCarte() {
        return carte;
    }
    
}
