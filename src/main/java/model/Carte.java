package model;

import java.time.LocalTime;
import java.util.*;

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

    public void majTrajetDepuisChemin(GrapheTotal gt, List<Long> chemin, Trajet trajet){
        List<Troncon> troncons = new ArrayList<Troncon>();
        float dureeTrajet = 0;
        for (int i= 0; i<chemin.size()-1; ++i){
            long idNoeud1 = chemin.get(i);
            long idNoeud2 = chemin.get(i+1);
            if (idNoeud1 != idNoeud2) {
                Troncon troncon = gt.NoeudstoTroncon(idNoeud1, idNoeud2);
                dureeTrajet += (troncon.getLongueur()/1000)/15;
                troncons.add(troncon);
            }
            
            Site siteTrouve = null;
            for(Site site: trajet.getSites()){
                if(site.getId() == idNoeud2){
                    siteTrouve = site;
                    break;
                }
            }
            if(siteTrouve != null)
            {   float heure_arrivee = 8 + dureeTrajet;

                if(siteTrouve instanceof Collecte) {
                    siteTrouve.setArriveeHeure(LocalTime.of((int)heure_arrivee,(int)((heure_arrivee%1)*60)));
                    dureeTrajet += ((Collecte)siteTrouve).getDureeRecup()/3600f;
                    float heure_depart = 8 + dureeTrajet;
                    siteTrouve.setDepartHeure(LocalTime.of((int)heure_depart,(int)((heure_depart%1)*60)));
                }

                else if(siteTrouve instanceof Depot) {
                    siteTrouve.setArriveeHeure(LocalTime.of((int)heure_arrivee,(int)((heure_arrivee%1)*60)));
                    dureeTrajet += ((Depot)siteTrouve).getDureeRecup()/3600f;
                    float heure_depart = 8 + dureeTrajet;
                    siteTrouve.setDepartHeure(LocalTime.of((int)heure_depart,(int)((heure_depart%1)*60)));
                }

                else if(i==chemin.size()-2){
                    siteTrouve.setArriveeHeure(LocalTime.of((int)heure_arrivee,(int)((heure_arrivee%1)*60)));
                }
                System.out.println("Site : " + siteTrouve.getId());
                System.out.println("Arrivée Sur site: " +siteTrouve.getArriveeHeure());
                System.out.println("Départ du site: " +siteTrouve.getDepartHeure());
            }
            
        }

        trajet.setTroncons(troncons);
        trajet.setdureeTrajet(dureeTrajet);
    }
}
