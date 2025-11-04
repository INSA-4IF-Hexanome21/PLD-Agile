package controller.state;

import java.util.HashMap;
import java.util.List;

import controller.CarteController;

public class LivreurAssigneState implements State {
    // État: livreur assigné - peut calculer, recharger carte/livraison, réassigner des livreurs ou calculer livraison
    
    @Override
   public boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
		System.out.println(">>> [LivreurAssigneState] Rechargement de la carte...");
		// Nettoyer livraisons et calculs précédents
		carteC.effacerLivraison();
		boolean chargementCarteReussi = carteC.chargerCarteDepuisXML(cheminFichier);
        if (chargementCarteReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [LivreurAssigneState] Transition vers CarteChargeState");
            return true;
        } else {
            c.setCurrentState(c.initialState);
            return false;
        }
    }

  @Override
    public boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivreurAssigneState] Rechargement de la livraison...");

        // Effacer la livraison précédente avant de charger la nouvelle
        System.out.println(">>> [LivreurAssigneState] Effacement des livraisons précédentes...");
        carteC.effacerLivraison();

        // Charger la nouvelle demande (idempotent après effacerLivraison)
        boolean chargementLivrasonReussi = carteC.chargerDemandesDepuisXML(cheminFichier);
        if (chargementLivrasonReussi == true){
            c.setCurrentState(c.livraisonChargeState);
            System.out.println(">>> [LivreurAssigneState] Transition vers LivraisonChargeState");
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
        System.out.println(">>> [LivreurAssigneState] Transition vers LivreurAssigneState");
    }



    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.out.println(">>> [LivreurAssigneState] Calcul de la livraison...");

        //Calcul la tournée
        try {
            System.out.println(">>> [LivreurAssigneState] Lancement du calcul de la tournée...");
            carteC.calculerTournee(); 
            c.setCurrentState(c.livraisonCalculeState);
            System.out.println(">>> [LivreurAssigneState] Calcul terminé, transition vers LivraisonCalculeState");
        } catch (Exception ex) {
            System.err.println(">>> [LivreurAssigneState] ERREUR pendant le calcul: " + ex.getMessage());
            throw new RuntimeException("Erreur lors du calcul de la tournée : " + ex.getMessage(), ex);
        }
        c.setCurrentState(c.livraisonCalculeState);
        System.out.println(">>> [LivreurAssigneState] Transition vers LivraisonCalculeState");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [LivreurAssigneState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer la livraison");
    }
}