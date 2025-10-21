package controller.state;

import controller.CarteController;

public class InitialState implements State {
    // Etat initial - seule action possible: charger une carte
    
    @Override
    public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [InitialState] Chargement de la carte...");
        carteC.chargerCarteDepuisXML(cheminFichier);
        c.setCurrentState(c.carteChargeState);
        System.out.println(">>> [InitialState] Transition vers CarteChargeState");
    }
    
    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
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