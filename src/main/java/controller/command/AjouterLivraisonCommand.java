package controller.command;

import model.Carte;
import model.GrapheTotal;
import model.Site;
import model.Trajet;

public class AjouterLivraisonCommand implements Command {
    
    private GrapheTotal gt;
    private Long idCollecte;
    private Long idDepot;
    private Trajet trajet;
    private Carte carte;

    public AjouterLivraisonCommand(GrapheTotal gt, Long idCollecte, Long idDepot, Trajet trajet, Carte carte) {
        this.gt = gt;
        this.idCollecte = idCollecte;
        this.idDepot = idDepot;
        this.trajet = trajet;
        this.carte = carte;
    }

    public void doCommand() {
        // TODO: to implement
    }

    public void undoCommand() {
        // TODO: to implement
    }
    
}
