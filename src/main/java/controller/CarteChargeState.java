package controller;


public class CarteChargeState implements State {
    @Override
	public void chargerCarte(Controller c, ServeurHTTP serveur) {

		//lancer charger carte
		serveur.chargerCarte();
		c.setCurrentState(c.carteChargeState);
	}

    @Override
    public void chargerLivraison(Controller c, ServeurHTTP serveur){

        serveur.chargerLivraison();
		c.setCurrentState(c.livraisonChargeState);

    }
}
