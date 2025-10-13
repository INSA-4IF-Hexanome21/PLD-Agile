package controller;

import model.Carte;
import model.GestionnaireXML;
import model.Noeud;
import model.Troncon;

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

    public String getCarteJSON() {
    StringBuilder json = new StringBuilder();
    json.append("{\"noeuds\":[");
    for (int i = 0; i < carte.getNoeuds().size(); i++) {
        Noeud n = carte.getNoeuds().get(i);
        json.append(String.format("{\"id\":%d,\"lat\":%f,\"lng\":%f}",
                n.getId(), n.getLatitude(), n.getLongitude()));
        if (i < carte.getNoeuds().size() - 1) json.append(",");
    }
    json.append("],\"troncons\":[");
    for (int i = 0; i < carte.getTroncons().size(); i++) {
        Troncon t = carte.getTroncons().get(i);
        json.append(String.format("{\"from\":%d,\"to\":%d}",
                t.getOrigine().getId(), t.getDestination().getId()));
        if (i < carte.getTroncons().size() - 1) json.append(",");
    }
    json.append("]}");
    return json.toString();
    }


    public Carte getCarte() {
        return carte;
    }
    
}
