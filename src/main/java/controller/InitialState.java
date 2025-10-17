package controller;

public class InitialState implements State {
// Etat initial 
	@Override
	public void chargerCarte(Controller c, ServeurHTTP serveur) {

		//lancer charger carte
		serveur.chargerCarte();
		c.setCurrentState(c.carteChargeState);
	}
}
