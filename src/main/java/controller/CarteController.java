package controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import tsp.*;

import model.*;

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
    }

    public Carte getCarte() {
        return carte;
    }

    public GrapheTotal creerGrapheTotal(Carte carte, long idEntrepot){
        HashMap<Long, Noeud> noeuds = carte.getNoeuds();
        List<Troncon> troncons = carte.getTroncons();
        int nbSommets = noeuds.size();

        GrapheTotal gt = new GrapheTotal(nbSommets, troncons, noeuds, idEntrepot);
        gt.printGraphe();
        return gt;
    }

    public void chercherCheminsMin(GrapheTotal gt, List<Site> sites){
        long tempsDebut = System.currentTimeMillis();
        gt.RechercheDijkstra(sites);
        GrapheLivraison gl = new GrapheLivraison(carte.getSites().size(), gt.getMapDistances());
        TSP tsp = new TSP1();
        tsp.chercheSolution(60000, gl);
        System.out.print("Solution de longueur "+tsp.getCoutSolution()+" trouvee en "
					+(System.currentTimeMillis() - tempsDebut)+"ms : ");
        List<Integer> solution = new ArrayList<Integer>();
        for (int i=0; i<gl.getNbSommets(); i++) {
            solution.add(gl.getIdFromIndex(tsp.getSolution(i)));
		   
        }
        solution.add(solution.get(0));
        System.out.println(solution);
        List<Integer> cheminComplet = gt.getCheminComplet(solution);
        System.out.println("Chemin complet avec noeuds intermédiaires : " + cheminComplet);
        List<Long> cheminCompletConverti = gt.convertirCheminComplet(cheminComplet);
        System.out.println("Chemin complet avec noeuds intermédiaires convertis: " + cheminCompletConverti);
    }
}
