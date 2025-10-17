package controller.state;

import controller.CarteController;

public class InitialState implements State {
// Etat initial 
	@Override
	public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {

		carteC.chargerCarteDepuisXML(cheminFichier);
		c.setCurrentState(c.carteChargeState);
	}
}
