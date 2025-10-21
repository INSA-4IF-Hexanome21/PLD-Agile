package controller.state;

import controller.CarteController;

public class LivraisonCalculeState implements State {
    // État: livraison calculée - peut changer livraison, recharger carte/livraison
    
    @Override
    public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonCalculeState] Rechargement de la carte (effacement calcul et livraison)...");
        // Effacer calculs et livraisons antérieurs pour éviter données résiduelles
        carteC.effacerCalcul();
        carteC.effacerLivraison();
        carteC.chargerCarteDepuisXML(cheminFichier);
        c.setCurrentState(c.carteChargeState);
        System.out.println(">>> [LivraisonCalculeState] Transition vers CarteChargeState");
    }

    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonCalculeState] Rechargement de la livraison (effacement calcul)...");
        // Effacer le calcul actuel
        carteC.effacerCalcul();
        carteC.chargerDemandesDepuisXML(cheminFichier);
    }


    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.out.println(">>> [LivraisonCalculeState] Recalcul de la livraison...");
        try {
            carteC.calculerTournee();
            c.setCurrentState(c.livraisonCalculeState);
        } catch (Exception ex) {
            System.err.println(">>> [LivraisonCalculeState] ERREUR pendant le calcul: " + ex.getMessage());
            c.setCurrentState(c.carteChargeState);
            throw new RuntimeException("Erreur lors du calcul de la tournée : " + ex.getMessage(), ex);
        }
    }

    @Override
    public void changerLivraison(Controller c) {
        System.out.println(">>> [LivraisonCalculeState] Changement d'une livraison dans la tournée...");
        // TODO: Modifier une livraison de la tournée calculée
        // carteC.modifierLivraison();
        // Le calcul est invalidé, on retourne à LivraisonChargeState
        c.setCurrentState(c.livraisonChargeState);
        System.out.println(">>> [LivraisonCalculeState] Livraison modifiée, transition vers LivraisonChargeState");
    }
}