package controller.state;

import controller.CarteController;

public class LivraisonCalculeState implements State {
    @Override
	public void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {

		carteC.chargerCarteDepuisXML(cheminFichier);
		c.setCurrentState(c.carteChargeState);
	}

    @Override
    public void chargerLivraison(Controller c, CarteController carteC, String cheminFichier){
        //effacerCalcul();
		carteC.chargerDemandesDepuisXML(cheminFichier);
		c.setCurrentState(c.livraisonChargeState);
    }

	@Override
    public void changerLivraison(Controller c){
        //lancer changer une livraison
		c.setCurrentState(c.livraisonChargeState);

    }
	
}

