package controller.state;

import controller.CarteController;


public class Controller{
	private State currentState;
	private CarteController carteController;

     // Instances associées avec chaque état possible du controlleur 
	protected final InitialState initialState = new InitialState();
	protected final CarteChargeState carteChargeState = new CarteChargeState();
	protected final LivraisonChargeState livraisonChargeState = new LivraisonChargeState();
	protected final LivraisonCalculeState livraisonCalculeState = new LivraisonCalculeState();

    /**
	 * Constructeur du controlleur
	 * 
	 */
	public Controller() {
		currentState = initialState;
		carteController = new CarteController();
	}

    /**
	 * Change l'état courant du controlleur
	 * @param state le nouvel état
	 */
	protected void setCurrentState(State state){
		currentState = state;
	}

	// Méthode des évenements
	/**
	 * Méthode pour charger la carte
	 */
	public boolean chargerCarte(String cheminFichier) {
		return currentState.chargerCarte(this, carteController, cheminFichier);
	}

	/**
	 *Méthode pour charger une livraison
	 */
	public boolean chargerLivraison(String cheminFichier) {
		return currentState.chargerLivraison(this, carteController, cheminFichier);
	}

    /**
	 * Méthode pour calculer une livraison
	 */
    public void calculerLivraison() {
        currentState.calculerLivraison(this, carteController);
    }

	/**
	 * Méthode pour changer une livraison d'un trajet
	 */
	public void changerLivraison() {
        currentState.changerLivraison(this);
    }

	 public String getCarteJSON() {
		return carteController.getCarteJSON();
	 }
}
