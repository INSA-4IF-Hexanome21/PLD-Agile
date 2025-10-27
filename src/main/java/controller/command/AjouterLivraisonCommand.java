package controller.command;

import model.Carte;
import model.GrapheTotal;
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
        System.out.println("Ajout de la collecte " + idCollecte + " et du depot " + idDepot);
        carte.ajouterLivraison(
            gt, 
            idCollecte, 
            idPrecCollecte, 
            dureeEnlevement, 
            idDepot, 
            idPrecDepot, 
            dureeLivraison, 
            trajet, 
            carte
        );
    }

    public void undoCommand() {
        System.out.println("Suppression de la collecte " + idCollecte + " et du depot " + idDepot);
        carte.supprimerLivraison(gt, idCollecte, idDepot, trajet, carte);
    }
    
}
