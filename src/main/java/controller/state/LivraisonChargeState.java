package controller.state;

import java.util.HashMap;
import java.util.List;

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
            c.setCurrentState(c.livraisonChargeState);
            System.out.println(">>> [LivraisonChargeState] Transition vers LivraisonChargeState");
            return true;
        } else {
            c.setCurrentState(c.carteChargeState);
            return false;
        }
    }

     @Override
    public void assignerLivreur(Controller c, CarteController carteC, HashMap<String, List<String>> assignations) {
        carteC.assignerLivreurs(assignations);
        c.setCurrentState(c.livreurAssigneState);
        System.out.println(">>> [LivraisonChargeState] Transition vers LivreurAssigneState");
    }

    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.err.println(">>> [InitialState] ERREUR: Impossible de calculer sans carte et livraison!");
        throw new IllegalStateException("Veuillez d'abord charger une carte et une livraison");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [LivraisonChargeState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer la livraison");
    }

    @Override
    public void genererFeuillesdeRoute(Controller c, CarteController carteC) {
        System.err.println(">>> [LivraisonChargeState] ERREUR: Impossible de générer des feuilles de route sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer la livraison");
    }
}