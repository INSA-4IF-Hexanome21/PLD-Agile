package controller.state;

import controller.CarteController;

public class LivraisonChargeState implements State {
    // État: livraison chargée - peut recalculer, recharger carte/livraison, ou calculer livraison
    
    @Override
    public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonChargeState] Rechargement de la carte (effacement livraison)...");
        // Effacer la livraison actuelle
        // carteC.effacerLivraison(); // À implémenter dans CarteController
        carteC.chargerCarteDepuisXML(cheminFichier);
        c.setCurrentState(c.carteChargeState);
        System.out.println(">>> [LivraisonChargeState] Transition vers CarteChargeState");
    }

    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonChargeState] Rechargement de la livraison...");
        carteC.chargerDemandesDepuisXML(cheminFichier);
        c.setCurrentState(c.livraisonChargeState);
        System.out.println(">>> [LivraisonChargeState] Livraison rechargée, reste dans LivraisonChargeState");
    }

    @Override
    public void calculerLivraison(Controller c) {
        System.out.println(">>> [LivraisonChargeState] Calcul de la livraison...");
        // TODO: Lancer le calcul de la tournée
        // carteC.calculerTournee();
        c.setCurrentState(c.livraisonCalculeState);
        System.out.println(">>> [LivraisonChargeState] Transition vers LivraisonCalculeState");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [LivraisonChargeState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer la livraison");
    }
}