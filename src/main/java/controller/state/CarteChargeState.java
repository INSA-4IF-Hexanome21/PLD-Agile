package controller.state;

import controller.CarteController;

public class CarteChargeState implements State {
    // État: carte chargée - peut charger une nouvelle carte ou une livraison
    
    @Override
    public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [CarteChargeState] Rechargement de la carte...");
        carteC.chargerCarteDepuisXML(cheminFichier);
        c.setCurrentState(c.carteChargeState);
        System.out.println(">>> [CarteChargeState] Carte rechargée, reste dans CarteChargeState");
    }

    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [CarteChargeState] Chargement de la livraison...");
        carteC.chargerDemandesDepuisXML(cheminFichier);
        c.setCurrentState(c.livraisonChargeState);
        System.out.println(">>> [CarteChargeState] Transition vers LivraisonChargeState");
    }
    
    @Override
    public void calculerLivraison(Controller c) {
        System.err.println(">>> [CarteChargeState] ERREUR: Impossible de calculer sans livraison!");
        throw new IllegalStateException("Veuillez d'abord charger une livraison");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [CarteChargeState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer une livraison");
    }
}