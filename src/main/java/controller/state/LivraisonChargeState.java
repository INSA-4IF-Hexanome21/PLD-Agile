package controller.state;

import controller.ServeurHTTP;

public class LivraisonChargeState implements State {
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

	@Override
    public void calculerLivraison(Controller c){

        //lancer calcul
		c.setCurrentState(c.livraisonChargeState);

    }
}
