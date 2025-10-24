package controller.state;

import controller.CarteController;

public class CarteChargeState implements State {
    // État: carte chargée - peut charger une nouvelle carte ou une livraison
    
   @Override
	public boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
		System.out.println(">>> [CarteChargeState] Rechargement de la carte...");
		boolean chargementCarteReussi = carteC.chargerCarteDepuisXML(cheminFichier);
        if (chargementCarteReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [CarteChargeState] Transition vers CarteChargeState");
            return true;
        } else {
            c.setCurrentState(c.initialState);
            return false;
        }
    }


   
    @Override
    public boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [CarteChargeState] Chargement de la livraison depuis " + cheminFichier);
        // Charger la demande
        boolean chargementLivrasonReussi = carteC.chargerDemandesDepuisXML(cheminFichier);
        if (chargementLivrasonReussi == true){
            c.setCurrentState(c.livraisonChargeState);
            System.out.println(">>> [CarteChargeState] Transition vers LivraisonChargeState");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.err.println(">>> [CarteChargeState] ERREUR: Impossible de calculer sans livraison!");
        throw new IllegalStateException("Veuillez d'abord charger une livraison");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [CarteChargeState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer une livraison");
    }
}