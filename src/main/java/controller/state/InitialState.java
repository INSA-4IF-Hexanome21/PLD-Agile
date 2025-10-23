package controller.state;

import controller.CarteController;

public class InitialState implements State {
    // Etat initial - seule action possible: charger une carte
    
    @Override
    public boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [InitialState] Chargement de la carte...");
        boolean chargementCarteReussi = carteC.chargerCarteDepuisXML(cheminFichier);
        if (chargementCarteReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [InitialState] Transition vers CarteChargeState");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.err.println(">>> [InitialState] ERREUR: Impossible de charger une livraison sans carte!");
        throw new IllegalStateException("Veuillez d'abord charger une carte");
    }
    
    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.err.println(">>> [InitialState] ERREUR: Impossible de calculer sans carte et livraison!");
        throw new IllegalStateException("Veuillez d'abord charger une carte et une livraison");
    }
    
    @Override
    public void changerLivraison(Controller c) {
        System.err.println(">>> [InitialState] ERREUR: Impossible de changer une livraison sans calcul!");
        throw new IllegalStateException("Veuillez d'abord calculer une livraison");
    }
}