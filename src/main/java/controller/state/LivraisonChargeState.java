package controller.state;

import controller.CarteController;

public class LivraisonChargeState implements State {
    // État: livraison chargée - peut recalculer, recharger carte/livraison, ou calculer livraison
    
    @Override
   public boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
		System.out.println(">>> [LivraisonChargeState] Rechargement de la carte...");
		// Nettoyer livraisons et calculs précédents
		carteC.effacerLivraison();
		boolean chargementCarteReussi = carteC.chargerCarteDepuisXML(cheminFichier);
        if (chargementCarteReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [LivraisonChargeState] Transition vers CarteChargeState");
            return true;
        } else {
            c.setCurrentState(c.initialState);
            return false;
        }
    }

  @Override
    public boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonChargeState] Rechargement de la livraison...");

        // Effacer la livraison précédente avant de charger la nouvelle
        System.out.println(">>> [LivraisonChargeState] Effacement des livraisons précédentes...");
        carteC.effacerLivraison();

        // Charger la nouvelle demande (idempotent après effacerLivraison)
        boolean chargementLivrasonReussi = carteC.chargerDemandesDepuisXML(cheminFichier);
        if (chargementLivrasonReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [LivraisonChargeState] Transition vers LivraisonChargeState");
            return true;
        } else {
            c.setCurrentState(c.carteChargeState);
            return false;
        }
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