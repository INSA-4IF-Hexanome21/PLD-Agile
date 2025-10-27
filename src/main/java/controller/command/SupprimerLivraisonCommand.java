package controller.command;

import java.util.List;

import model.Carte;
import model.GrapheTotal;
import model.Trajet;
import model.utils.CarteUtils;
import model.Collecte;
import model.Depot;

public class SupprimerLivraisonCommand implements Command {
    
    private GrapheTotal gt;
    private Collecte collecte;
    private Depot depot;
    private Trajet trajet;
    private Carte carte;
    private List<Long> solution;

    public SupprimerLivraisonCommand(GrapheTotal gt, Collecte collecte, Depot depot, Trajet trajet, Carte carte) {
        this.gt = gt;
        this.collecte = collecte;
        this.depot = depot;
        this.trajet = trajet;
        this.carte = carte;
        this.solution = trajet.getSolution();
    }

    public void doCommand() {
        System.out.println("Suppression de la collecte "+ collecte.getId() +" et du depot "+ depot.getId());
        carte.supprimerLivraison(gt, collecte.getId(), depot.getId(), trajet, carte);
    }

    public void undoCommand() {
        System.out.println("Ajout de la collecte "+ collecte.getId() +" et du depot "+ depot.getId());
        carte.ajouterLivraison(gt, collecte.getId(), CarteUtils.getPrecSite(collecte, solution), collecte.getDureeRecup(), depot.getId(), CarteUtils.getPrecSite(depot, solution), depot.getDureeRecup(), trajet, carte);
    }
    
}
