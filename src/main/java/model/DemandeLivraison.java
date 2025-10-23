package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class DemandeLivraison {

    // Attributs
    private Entrepot entrepot;
    private HashMap<Long,Site> sites;
    private HashMap<Integer,List<Long>> livraisons;
    private HashMap<Integer,Boolean> livraisonsAssignees;
    private Integer nbLivraisonsNonAssignees;


    // Constructeur complet
    public DemandeLivraison() {
        this.sites = new HashMap<Long,Site>();
        this.livraisons = new HashMap<Integer,List<Long>>();
        this.livraisonsAssignees = new HashMap<Integer,Boolean>();
        this.nbLivraisonsNonAssignees = 0;
        this.entrepot = null;
    }

    // Getters et Setters
    public Site getSite(Long idSite){
        return sites.get(idSite);
    }

    public Integer assignerLivreur(Livreur livreur, Integer idLivraison, Carte carte ){
        Trajet trajet = carte.getTrajetParLivreur(livreur.getId());
        List<Long> idsSiteslivraison = livraisons.get(idLivraison);
        if(!livraisonsAssignees.get(idLivraison)){
            livraisonsAssignees.put(idLivraison,true);
            --this.nbLivraisonsNonAssignees;
        }
        if(trajet == null){
            trajet = new Trajet(livreur);
            trajet.addSite(entrepot);
            carte.ajouterTrajet(trajet);
        }
        else{
            trajet.setLivreur(livreur);
        }
        for(Long id : idsSiteslivraison){
            trajet.addSite(sites.get(id));
        }
        return this.nbLivraisonsNonAssignees;
    }

    public void addSite(Site site, boolean estEntrepot){
        Long idSite = site.getId();
        if(estEntrepot){
            this.entrepot = (Entrepot)site;
        }
        else{
            Integer numLivraison;
            if(site instanceof Collecte){
                numLivraison = ((Collecte)site).getNumLivraison();
            }
            else{
                numLivraison = ((Depot)site).getNumLivraison();
            }
            if(livraisons.keySet().contains(numLivraison)){
                livraisons.get(numLivraison).add(idSite);
            }
            else{
                List<Long> idsSites = new ArrayList<Long>();
                idsSites.add(idSite);
                livraisons.put(numLivraison,idsSites);
                livraisonsAssignees.put(numLivraison,false);
                ++nbLivraisonsNonAssignees;
            }
        }
        sites.put(idSite,site);
    }

    public List<Site> getSites(){
        return new ArrayList<Site>(sites.values());
    }


    
}