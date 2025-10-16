package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import tsp.Graphe;

public class Carte {

    // Attributs
    private HashMap<Long, Noeud> noeuds;
    private List<Troncon> troncons;
    private List<Site> sites;
    private List<Trajet> trajets;

    // Constructeur
    public Carte() {
        this.noeuds = new HashMap<>();
        this.troncons = new ArrayList<>();
        this.sites = new ArrayList<>();
        this.trajets = new ArrayList<>();
    }

    // Getters et Setters
    public HashMap<Long, Noeud> getNoeuds() {
        return noeuds;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public List<Site> getSites() {
        return sites;
    }

    public List<Trajet> getTrajets() {
        return trajets;
    }

    public void setNoeuds(HashMap<Long, Noeud> noeuds) {
        this.noeuds = noeuds;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }

    // Méthodes 
    public void ajouterNoeud(Noeud noeud) {
        if (!noeuds.containsKey(noeud.getId())) this.noeuds.put(noeud.getId(), noeud);
    }

    public void ajouterTroncon(Troncon troncon) {
        if (!troncons.contains(troncon)) this.troncons.add(troncon);
    }

    public void ajouterSite(Site site) {
        this.sites.add(site);
    }

    public void supprimerSite(Site site) {
        this.sites.remove(site);
    }

    public void ajouterTrajet(Trajet trajet) {
        this.trajets.add(trajet);
    }

    public void supprimerTrajet(Trajet trajet) {
        this.trajets.remove(trajet);
    }

    public void RechercheDijkstra(GrapheTotal gt) {
        Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin = new HashMap<>();
        Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances = new HashMap<>();

        for (Site siteDepart : sites) {
            Map<Integer, Float> distancesDepuisDepart = Dijsktra.dijkstra(gt, siteDepart.getId(), cheminsMin);
            
            //Stocker les couts vers les autres sites
            List<SimpleEntry<Integer, Float>> voisins = new ArrayList<>();
            for (Site siteArrivee : sites) {
                if (siteDepart != siteArrivee) {
                    Float cout = distancesDepuisDepart.get(gt.idToIndex.get(siteArrivee.getId()));
                    if (cout != null) {
                        voisins.add(new SimpleEntry<>(gt.idToIndex.get(siteArrivee.getId()), cout));
                    }
                }
            }
            mapDistances.put(gt.idToIndex.get(siteDepart.getId()), voisins);
        }
        System.out.println("Map Distances entre sites : " + mapDistances);
    }
}
