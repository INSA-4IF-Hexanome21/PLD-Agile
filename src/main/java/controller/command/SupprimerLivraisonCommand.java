package controller.command;

import model.Carte;
import model.GrapheTotal;
import model.Trajet;

public class SupprimerLivraisonCommand implements Command {
    
    private GrapheTotal gt;
    private Long idCollecte;
    private Long idDepot;
    private Trajet trajet;
    private Carte carte;

    public SupprimerLivraisonCommand(GrapheTotal gt, Long idCollecte, Long idDepot, Trajet trajet, Carte carte) {
        this.gt = gt;
        this.idCollecte = idCollecte;
        this.idDepot = idDepot;
        this.trajet = trajet;
        this.carte = carte;
    }

    public void doCommand() {
        carte.supprimerLivraison(gt, idCollecte, idDepot, trajet, carte);
    }

    public void undoCommand() {
        carte.ajouterLivraison(gt, idCollecte, idCollecte, null, idDepot, idDepot, null, trajet, carte);
    }
    
}
