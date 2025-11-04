package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Assignation {
    
     // Attributs
    private int nbLivreur;
    private HashMap<Long,List<Integer>> livraisons; //Hashmap associant le numéro d'une livraisons à l'id de son livreur
    // Constructeur
    public Assignation() {
        this.nbLivreur = 0;
        this.livraisons = new HashMap<Long,List<Integer>>();
    }

    // Getters et Setters
    public int getnbLivreur() {
        return nbLivreur;
    }

    public List<Integer> getLivraison(Long idLivreur){
        return livraisons.get(idLivreur);
    }

    public void setnbLivreur(int nbLivreur){
        this.nbLivreur = nbLivreur;
    }

    public void addLivraison(int idLivraison, long idLivreur){
         if(livraisons.keySet().contains(idLivreur)){
                livraisons.get(idLivreur).add(idLivraison);
            }
            else{
                List<Integer> idsLivraison = new ArrayList<Integer>();
                idsLivraison.add(idLivraison);
                livraisons.put(idLivreur,idsLivraison);
            }
    }
}
