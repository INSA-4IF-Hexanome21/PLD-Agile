package controller.state;

import controller.CarteController;

public class CarteChargeState implements State {
    @Override
	public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {

		carteC.chargerCarteDepuisXML(cheminFichier);
		c.setCurrentState(c.carteChargeState);
	}


    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier){

		carteC.chargerDemandesDepuisXML(cheminFichier);
		c.setCurrentState(c.livraisonChargeState);
    }
}
