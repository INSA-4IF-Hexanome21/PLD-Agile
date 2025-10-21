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

        // Effacer la livraison précédente avant de charger la nouvelle
        System.out.println(">>> [LivraisonChargeState] Effacement des livraisons précédentes...");
        carteC.effacerLivraison();

        // Charger la nouvelle demande (idempotent après effacerLivraison)
        carteC.chargerDemandesDepuisXML(cheminFichier);

    }



    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.out.println(">>> [LivraisonChargeState] Calcul de la livraison...");
        try {
            System.out.println(">>> [LivraisonChargeState] Lancement du calcul de la tournée...");
            carteC.calculerTournee(); 
            c.setCurrentState(c.livraisonCalculeState);
            System.out.println(">>> [LivraisonChargeState] Calcul terminé, transition vers LivraisonCalculeState");
        } catch (Exception ex) {
            System.err.println(">>> [LivraisonChargeState] ERREUR pendant le calcul: " + ex.getMessage());
            throw new RuntimeException("Erreur lors du calcul de la tournée : " + ex.getMessage(), ex);
        }
        c.setCurrentState(c.livraisonCalculeState);
        System.out.println(">>> [LivraisonChargeState] Transition vers LivraisonCalculeState");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [LivraisonChargeState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer la livraison");
    }
}