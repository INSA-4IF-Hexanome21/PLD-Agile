package controller.state;

import controller.CarteController;
import controller.ServeurHTTP;

public class Controller{
	private State currentState;
	private CarteController carteController;
	private ServeurHTTP serveur;

     // Instances associées avec chaque état possible du controlleur 
	protected final InitialState initialState = new InitialState();
	protected final CarteChargeState carteChargeState = new CarteChargeState();
	protected final LivraisonChargeState livraisonChargeState = new LivraisonChargeState();
	protected final LivraisonCalculeState livraisonCalculeState = new LivraisonCalculeState();

    /**
	 * Constructeur du controlleur
	 * 
	 */
	public Controller(CarteController carteController, ServeurHTTP serveur) {
		currentState = initialState;
		this.carteController = carteController;
		this.serveur = serveur;
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
	public void chargerCarte() {
		currentState.chargerCarte(this, serveur);
	}

	/**
	 *Méthode pour charger une livraison
	 */
	public void chargerLivraison() {
		currentState.chargerLivraison(this, serveur);
	}

    /**
	 * Méthode pour calculer une livraison
	 */
    public void calculerLivraison() {
        currentState.calculerLivraison(this);
    }

	/**
	 * Méthode pour changer une livraison d'un trajet
	 */
	public void changerLivraison() {
        currentState.calculerLivraison(this);
    }
}
