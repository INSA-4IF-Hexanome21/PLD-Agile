package controller.command;

import java.util.AbstractMap.SimpleEntry;

import controller.CarteController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import model.Carte;
import model.Collecte;
import model.Depot;
import model.GrapheTotal;
import model.Noeud;
import model.Site;
import model.Trajet;

public class AjouterLivraisonCommand implements Command {
    
    private GrapheTotal gt;
    private Long idCollecte;
    private Long idDepot;
    private Long idPrecCollecte;
    private Long idPrecDepot;
    private Integer dureeEnlevement;
    private Integer dureeLivraison;
    private Trajet trajet;
    private Carte carte;

    public AjouterLivraisonCommand(GrapheTotal gt, Long idCollecte,Long idPrecCollecte,Integer dureeEnlevement, Long idDepot,Long idPrecDepot, Integer dureeLivraison,Trajet trajet, Carte carte) {
        this.gt = gt;
        this.idCollecte = idCollecte;
        this.idDepot = idDepot;
        this.idPrecCollecte = idPrecCollecte;
        this.idPrecDepot = idPrecDepot;
        this.dureeEnlevement = dureeEnlevement;
        this.dureeLivraison = dureeLivraison;
        this.trajet = trajet;
        this.carte = carte;
    }

    public void doCommand() {
        CarteController carteController = new CarteController();

        //Creation des nouveaux sites
        Noeud noeudCollecte = gt.trouverNoeud(idCollecte);
        Collecte collecte = new Collecte(idCollecte, noeudCollecte.getLatitude(), noeudCollecte.getLongitude(), carte.getNbLivraisons()+1, dureeEnlevement);
        Noeud noeudDepot = gt.trouverNoeud(idDepot);
        Depot depot = new Depot(idDepot, noeudDepot.getLatitude(), noeudDepot.getLongitude(), carte.getNbLivraisons()+1, dureeLivraison);
        trajet.addSite(collecte);
        trajet.addSite(depot);
        carte.ajouterSite(collecte);
        carte.ajouterSite(depot);

        List<Long> solutionLong = trajet.getSolution();
        List<Long> nouvSolutionLong = new ArrayList<>();
        List<Long> nouvCheminComplet = new ArrayList<>();

        Integer indexCollecteSol = 0;
        Integer indexDepotSol = 0;

        for (int i = 0; i<solutionLong.size();++i){
            Long idSite = solutionLong.get(i);
            System.out.println();
            System.out.println("--------------------------------------------------");
            System.out.println("Site en cours :" + idSite);
            System.out.println("Site Collecte prec attendu  :" + idPrecCollecte);
            System.out.println("Site Depot prec attendu  :" + idPrecDepot);
            System.out.println("--------------------------------------------------");
            System.out.println();
            nouvSolutionLong.add(idSite);
            if(i<solutionLong.size() - 1 && Objects.equals(idSite, this.idPrecCollecte)){
                System.out.println("Ajout de la collecte");
                nouvSolutionLong.add(idCollecte);
                indexCollecteSol = i+1;
            }
            else if(i<solutionLong.size() - 1 && Objects.equals(idSite, this.idPrecDepot)){
                System.out.println("Ajout du depot");
                nouvSolutionLong.add(idDepot);
                indexDepotSol = i+2;
            }
        }


        //Recherche Dijkstra Collecte
        List<Site> siteARechercher = new ArrayList<Site>();
        siteARechercher.add(trajet.getSite(idCollecte));
        siteARechercher.add(trajet.getSite(idPrecCollecte)); // Ajout du Site précédent
        siteARechercher.add(trajet.getSite(nouvSolutionLong.get(indexCollecteSol + 1))); // Ajout du Site suivant

        siteARechercher.add(trajet.getSite(idDepot));
        siteARechercher.add(trajet.getSite(idPrecDepot)); // Ajout du Site précédent
        siteARechercher.add(trajet.getSite(nouvSolutionLong.get(indexDepotSol + 1))); // Ajout du Site suivant

        gt.RechercheDijkstra(siteARechercher);

        //Recréation du chemin
        for (var i = 0; i<nouvSolutionLong.size()-1;++i) {
            List<Long> chemin = carteController.getChemin(gt, nouvSolutionLong.get(i), nouvSolutionLong.get(i+1));
            carteController.ajoutSansDuplication(nouvCheminComplet,chemin);
        }

        // conversion de la solution pour passage a majTrajet
        List<Integer> solution = new ArrayList<>();
        for (var id : nouvSolutionLong) {
            solution.add(gt.getIndexFromId(id));
        }
        System.out.println("Nouvelle solution : " + solution);
        carteController.majTrajet(carte, gt, nouvCheminComplet, solution, trajet);
    }

    public void undoCommand() {
        // TODO: to implement
    }
    
}
