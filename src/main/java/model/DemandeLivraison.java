package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Classe permettant de stocker une demande de livraison
public class DemandeLivraison {

    // Attributs
    private Entrepot entrepot;
    private HashMap<Long,Site> sites;
    private HashMap<Integer,List<Long>> livraisons; //Hashmap associant le numéro d'une livraisons aux id des sites de collectes et de dépots
    private HashMap<Integer,Boolean> livraisonsAssignees; // Hashmap permettant de savoir si une livraison est assigné à partir de son numéro de livraison
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

     public List<Site> getSites(){
        return new ArrayList<Site>(sites.values());
    }

    //Fonction permettant d'assigner un livreur à une livraison
    //Elle renvoie le nombre de livraisons qu'il reste à assigner
    public Integer assignerLivreur(Livreur livreur, Integer idLivraison, Carte carte ){
        
        List<Long> idsSiteslivraison = livraisons.get(idLivraison);
        //On vérifie si la livraison a déjà été assignée si ce n'est pas le cas on la défini comme assignée
        if(!livraisonsAssignees.get(idLivraison)){
            livraisonsAssignees.put(idLivraison,true);
            --this.nbLivraisonsNonAssignees;
            carte.setNbLivraisons(carte.getNbLivraisons()+1);
        }
        
        Trajet trajet = carte.getTrajetParLivreur(livreur.getId());
        
        if(trajet == null){
            trajet = new Trajet(livreur);
            trajet.addSite(entrepot);
            carte.ajouterTrajet(trajet);
        }
        else{
            trajet.setLivreur(livreur);
        }

        //On ajoute les sites associés à la livraison dans le trajet associé au livreur
        for(Long id : idsSiteslivraison){
            trajet.addSite(sites.get(id));
        }
        return this.nbLivraisonsNonAssignees;
    }

    //Permet d'ajouter un site
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

                //Il s'agit d'une nouvelle livraison donc on la défini comme non assignée
                livraisonsAssignees.put(numLivraison,false);
                ++nbLivraisonsNonAssignees;
            }
        }
        sites.put(idSite,site);
    }    
}