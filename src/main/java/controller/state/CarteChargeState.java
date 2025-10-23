package controller.state;

import controller.CarteController;

public class CarteChargeState implements State {
    // État: carte chargée - peut charger une nouvelle carte ou une livraison
    
   @Override
	public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
		System.out.println(">>> [CarteChargeState] Rechargement de la carte...");
		// Nettoyer livraisons et calculs précédents
		carteC.effacerCalcul();
		carteC.effacerLivraison();
		carteC.chargerCarteDepuisXML(cheminFichier);
		c.setCurrentState(c.carteChargeState);
		System.out.println(">>> [CarteChargeState] Carte rechargée, reste dans CarteChargeState");
	}


   
    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [CarteChargeState] Chargement de la livraison depuis " + cheminFichier);
        // Charger la demande
        carteC.chargerDemandesDepuisXML(cheminFichier);

        // Lancer immédiatement le calcul de la tournée
        try {
            System.out.println(">>> [CarteChargeState] Lancement du calcul de la tournée...");
            carteC.calculerTournee(); // <-- méthode que tu as créée dans CarteController
            c.setCurrentState(c.livraisonCalculeState);
            System.out.println(">>> [CarteChargeState] Calcul terminé, transition vers LivraisonCalculeState");
        } catch (Exception ex) {
            System.err.println(">>> [CarteChargeState] ERREUR pendant le calcul: " + ex.getMessage());
            ex.printStackTrace();
            // On reste en CarteChargeState (ou on peut définir une stratégie d'erreur différente)
            c.setCurrentState(c.carteChargeState);
            // Renvoyer l'erreur pour que le serveur HTTP puisse la remonter si nécessaire
            throw new RuntimeException("Erreur lors du calcul de la tournée : " + ex.getMessage(), ex);
        }
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