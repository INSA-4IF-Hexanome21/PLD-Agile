package controller.state;

import java.util.HashMap;
import java.util.List;

import controller.CarteController;

public class LivraisonCalculeState implements State {
    // État: livraison calculée - peut changer livraison, recharger carte/livraison
    
    @Override
	public boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
		System.out.println(">>> [LivraisonCalculeState] Rechargement de la carte...");
		// Nettoyer livraisons et calculs précédents
		carteC.effacerCalcul();
		carteC.effacerLivraison();
		boolean chargementCarteReussi = carteC.chargerCarteDepuisXML(cheminFichier);
        if (chargementCarteReussi == true){
            c.setCurrentState(c.carteChargeState);
            System.out.println(">>> [LivraisonCalculeState] Transition vers LivraisonChargeState");
            return true;
        } else {
            c.setCurrentState(c.initialState);
            return false;
        }
    }

    @Override
    public boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        System.out.println(">>> [LivraisonCalculeState] Rechargement de la livraison (effacement calcul)...");
        // Effacer le calcul actuel
        carteC.effacerCalcul();
        boolean chargementLivrasonReussi = carteC.chargerDemandesDepuisXML(cheminFichier);
        if (chargementLivrasonReussi == true){
            c.setCurrentState(c.livraisonCalculeState);
            System.out.println(">>> [LivraisonCalculeState] Transition vers CarteChargeState");
            return true;
        } else {
            c.setCurrentState(c.carteChargeState);
            return false;
        }
    }

    @Override
    public void assignerLivreur(Controller c, CarteController carteC, HashMap<String, List<String>> assignations) {
        carteC.effacerCalcul();
        carteC.assignerLivreurs(assignations);
        c.setCurrentState(c.livreurAssigneState);
    }

    @Override
    public void calculerLivraison(Controller c, CarteController carteC) {
        System.out.println(">>> [LivraisonCalculeState] Recalcul de la livraison...");

        //Calcul la tournée
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
    public void genererFeuillesdeRoute(Controller c, CarteController carteC) {
        System.out.println(">>> [LivraisonCalculeState] génération des feuilles de routes...");
        carteC.genererFeuillesdeRoute();
        c.setCurrentState(c.livraisonCalculeState);
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

    @Override
    public void ajouterLivraison(Controller c, CarteController carteC, String idCollecte,String idPrecCollecte,String idDepot,String idPrecDepot,String numTrajet) {
        carteC.ajouterLivraison(Long.valueOf(idCollecte), Long.valueOf(idPrecCollecte), Long.valueOf(idDepot), Long.valueOf(idPrecDepot), carteC.getCarte().getTrajets().get(Integer.valueOf(numTrajet)));
    }
}