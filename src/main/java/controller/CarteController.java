package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import model.Carte;
import model.Noeud;
import model.Troncon;

public class CarteController {
    private Carte carte;
    private GestionnaireXML gestionnaireXML;

    public CarteController() {
        this.carte = new Carte();
        this.gestionnaireXML = new GestionnaireXML();
    }

    public void chargerCarteDepuisXML(String cheminFichier) {
    if (gestionnaireXML == null) {
        gestionnaireXML = new GestionnaireXML(); 
    } 
    HashMap<Long, Noeud> noeuds = gestionnaireXML.chargerPlanNoeuds(cheminFichier);
    if (carte == null) {
        carte = new Carte();
    }
    carte.setNoeuds(noeuds);
    List<Troncon> troncons = gestionnaireXML.chargerPlanTroncons(cheminFichier,noeuds);
    carte.setTroncons(troncons);
}

    public String getCarteJSON() {
    StringBuilder json = new StringBuilder();
    json.append("{\"noeuds\":[");
    int noeudCount = 0;
    int noeudTotal = carte.getNoeuds().size();
    for (Noeud n : carte.getNoeuds().values()) {
        json.append(String.format(Locale.US,"{\"id\":%d,\"lat\":%f,\"lng\":%f}",
                n.getId(), n.getLatitude(), n.getLongitude()));
        noeudCount++;
        if (noeudCount < noeudTotal) json.append(",");
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
