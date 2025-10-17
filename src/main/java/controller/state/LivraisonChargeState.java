package controller.state;

import controller.CarteController;

public class LivraisonChargeState implements State {
    @Override
	public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {

		carteC.chargerCarteDepuisXML(cheminFichier);
        //carteC.effacerLivraison(); //à faire
		c.setCurrentState(c.carteChargeState);
	}

    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier){

		carteC.chargerDemandesDepuisXML(cheminFichier);
		c.setCurrentState(c.livraisonChargeState);
    }

	@Override
    public void calculerLivraison(Controller c){
        //lancer calcul
		c.setCurrentState(c.livraisonChargeState);

    }
}
