package controller;

<<<<<<< HEAD
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
=======
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import model.Carte;
import model.Noeud;
import model.Site;
import model.Trajet;
import model.Troncon;

public class CarteController {
    private Carte carte;

    public CarteController() {
        this.carte = new Carte();
    }

    /**
     * Charge le plan (noeuds et troncons) depuis un fichier XML
     */
    public void chargerCarteDepuisXML(String cheminFichier) {
        if (carte == null) {
            carte = new Carte();
        }
        
        HashMap<Long, Noeud> noeuds = GestionnaireXML.chargerPlanNoeuds(cheminFichier);
        carte.setNoeuds(noeuds);
        
        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminFichier, noeuds);
        carte.setTroncons(troncons);
        
        System.out.println("Plan chargé: " + noeuds.size() + " noeuds, " + troncons.size() + " troncons");
    }
    
    /**
     * Charge les demandes de livraison depuis un fichier XML
     */
    public void chargerDemandesDepuisXML(String cheminFichierDemandes) {
        if (carte == null) {
            carte = new Carte();
        }
        
        Trajet trajet = GestionnaireXML.chargerDemandeLivraisons(
            cheminFichierDemandes, 
            carte.getNoeuds()
        );
        
        carte.ajouterTrajet(trajet);
        
        for (Site site : trajet.getSites()) {
            carte.ajouterSite(site);
        }
        
        System.out.println("Demandes chargées:");
        System.out.println("  - Sites accessibles: " + trajet.getSites().size());
        System.out.println("  - Sites NON accessibles: " + trajet.getSitesNonAccessibles().size());
        System.out.println("  - Total sites dans carte: " + carte.getSites().size());
    }
    
    /**
     * Génère le JSON complet de la carte avec noeuds, troncons et sites
     */
    public String getCarteJSON() {
        System.out.println(">>> getCarteJSON appelé <<<");
        
        StringBuilder json = new StringBuilder();
        json.append("{\"noeuds\":[");
        
        // Noeuds
        int noeudCount = 0;
        int noeudTotal = carte.getNoeuds().size();
        for (Noeud n : carte.getNoeuds().values()) {
            json.append(String.format(Locale.US,"{\"id\":%d,\"lat\":%f,\"lng\":%f}",
                    n.getId(), n.getLatitude(), n.getLongitude()));
            noeudCount++;
            if (noeudCount < noeudTotal) json.append(",");
        }
        
        // Troncons
        json.append("],\"troncons\":[");
        for (int i = 0; i < carte.getTroncons().size(); i++) {
            Troncon t = carte.getTroncons().get(i);
            json.append(String.format("{\"from\":%d,\"to\":%d}",
                    t.getOrigine().getId(), t.getDestination().getId()));
            if (i < carte.getTroncons().size() - 1) json.append(",");
        }
        
        // Sites
        json.append("],\"sites\":[");
        List<Site> sites = carte.getSites();
        System.out.println("Génération JSON pour " + sites.size() + " sites");
        
        for (int i = 0; i < sites.size(); i++) {
            Site s = sites.get(i);
            System.out.println("Traitement site " + (i+1) + "/" + sites.size() + " - ID: " + s.getId());
            
            try {
                // Utiliser getLat() et getLng() directement (types primitifs)
                double lat = s.getLat();
                double lng = s.getLng();
                
                json.append(String.format(Locale.US, 
                    "{\"id\":%d,\"lat\":%f,\"lng\":%f,\"type\":\"%s\"",
                    s.getId(), lat, lng, s.getTypeSite()));
                
                // Agregar horarios si existen
                if (s.getDepartHeure() != null) {
                    json.append(String.format(",\"depart\":\"%s\"", s.getDepartHeure().toString()));
                }
                if (s.getArriveeHeure() != null) {
                    json.append(String.format(",\"arrivee\":\"%s\"", s.getArriveeHeure().toString()));
                }
                
                json.append("}");
                
                // Agregar coma si no es el último elemento
                if (i < sites.size() - 1) {
                    json.append(",");
                }
                
                System.out.println("Site " + s.getId() + " traité avec succès");
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du site " + s.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        json.append("]}");
        System.out.println(">>> JSON généré avec succès - Longueur: " + json.length() + " caractères <<<");
        return json.toString();
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
    }

    public Carte getCarte() {
        return carte;
    }
<<<<<<< HEAD
    
}
=======
}
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
