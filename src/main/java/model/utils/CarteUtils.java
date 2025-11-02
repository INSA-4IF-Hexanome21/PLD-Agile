package model.utils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import model.Carte;
import model.Collecte;
import model.Depot;
import model.GrapheTotal;
import model.Site;
import model.Trajet;
import model.Troncon;

/* 
 * Classe qui rassemble les principales méthodes utilitaires en rapport avec la carte 
 */
public final class CarteUtils {

    //plage horaire de tolérance lors d'un changement en minute
    //Si heure initial = 9h50 alors changement acceptable si nouvelle heure
    //9h45<nouvelle_heure<9h55
    static Float PLAGEHORAIRE = 17f; 

    private CarteUtils() {}
    
    public static List<Long> getChemin(GrapheTotal gt, Long prec, Long suiv) {
        SimpleEntry<Integer, Integer> key = new SimpleEntry<>(
            gt.getIndexFromId(prec), 
            gt.getIndexFromId(suiv)
        );
        List<Integer> chemin = gt.getCheminsMin().get(key);
        if (chemin == null || chemin.isEmpty()) return Collections.emptyList();

        List<Long> cheminLong = gt.convertirCheminComplet(chemin);
        // System.out.println("De "+ prec +" à " +suiv+" : "+ cheminLong);
        return new ArrayList<>(cheminLong);
    }

    public static void ajoutSansDuplication(List<Long> cible, List<Long> ajout) {
        if (ajout.isEmpty()) return;
        if (!cible.isEmpty() && cible.get(cible.size() - 1).equals(ajout.get(0))) {
            cible.addAll(ajout.subList(1, ajout.size()));
        } else {
            cible.addAll(ajout);
        }
    }

    public static HashMap<Site,Long> majTrajet(Carte carte, GrapheTotal gt, List<Long> cheminComplet, List<Integer> solution, Trajet trajet){
       
        //Hashmap servant a stocké l'id d'un site impacté par un changement avec la différence avec la valeur initiale
        HashMap<Site,Long> sitesImpactes = new HashMap<Site,Long>(); 
        
        trajet.setCheminComplet(cheminComplet);

        List<Troncon> troncons = new ArrayList<Troncon>();
        float dureeTrajet = 0;
        //Création d'une hasmap pour numéro de passage
        HashMap<Long,Integer> numsPassage = new HashMap<Long,Integer>();
        List<Long> solutionLongs = new ArrayList<>();
        for(int i= 0; i<solution.size(); ++i){
            numsPassage.put(gt.getIdFromIndex(solution.get(i)),i);
            solutionLongs.add(gt.getIdFromIndex(solution.get(i)));
        }
        trajet.setSolution(solutionLongs);

        Integer indexSolution = 1;
        Long idSiteAttendu = gt.getIdFromIndex(solution.get(indexSolution));
        for (int i= 0; i<cheminComplet.size()-1; ++i){
            long idNoeud1 = cheminComplet.get(i);
            long idNoeud2 = cheminComplet.get(i+1);
            if (idNoeud1 != idNoeud2) {
                Troncon troncon = gt.NoeudstoTroncon(idNoeud1, idNoeud2);
                dureeTrajet += (troncon.getLongueur()/1000)/15;
                troncons.add(troncon);
            }
            
            Site siteTrouve = null;
            for(Site site: trajet.getSites()){
                if(site.getId() == idNoeud2){
                    siteTrouve = site;
                    if(site.getId() != idSiteAttendu){
                        siteTrouve = null;
                    }
                    else{
                        if(indexSolution < solution.size()-1)
                        {
                            indexSolution += 1;
                            idSiteAttendu = gt.getIdFromIndex(solution.get(indexSolution));
                        }
                        
                    }
                    break;
                }
            }
            if(siteTrouve != null)
            {   
                float heure_arrivee = 8 + dureeTrajet;
                LocalTime ancienneHeureArrivee = siteTrouve.getArriveeHeure();
                LocalTime nouvelleHeureArrivee = LocalTime.of((int)heure_arrivee,(int)((heure_arrivee%1)*60));
                if(ancienneHeureArrivee != null){
                    long difference = ChronoUnit.MINUTES.between(ancienneHeureArrivee,nouvelleHeureArrivee);
                    if(difference > PLAGEHORAIRE/2f){
                        sitesImpactes.put(siteTrouve,difference);
                    }
                }
                

                if(siteTrouve instanceof Collecte) {
                    siteTrouve.setArriveeHeure(nouvelleHeureArrivee);
                    dureeTrajet += ((Collecte)siteTrouve).getDureeRecup()/3600f;
                    float heure_depart = 8 + dureeTrajet;
                    siteTrouve.setDepartHeure(LocalTime.of((int)heure_depart,(int)((heure_depart%1)*60)));
                }

                else if(siteTrouve instanceof Depot) {
                    siteTrouve.setArriveeHeure(nouvelleHeureArrivee);
                    dureeTrajet += ((Depot)siteTrouve).getDureeRecup()/3600f;
                    float heure_depart = 8 + dureeTrajet;
                    siteTrouve.setDepartHeure(LocalTime.of((int)heure_depart,(int)((heure_depart%1)*60)));
                }

                siteTrouve.setNumPassage(numsPassage.get(siteTrouve.getId()));
                
                // System.out.println();
                // System.out.println("Site : " + siteTrouve.getId());
                // System.out.println("numPassage : " + siteTrouve.getNumPassage());
                // System.out.println("Arrivée Sur site: " +siteTrouve.getArriveeHeure());
                // System.out.println("Départ du site: " +siteTrouve.getDepartHeure());
            }
        }
        

        trajet.setTroncons(troncons);
        trajet.setdureeTrajet(dureeTrajet);
        return sitesImpactes;
    }

    public static long getPrecSite(Site site, List<Long> solution) {
        
        int indexSite = solution.indexOf(site.getId());
        if (indexSite < 1) {
            return -1L;
        }
        return solution.get(indexSite - 1);
    }

}
