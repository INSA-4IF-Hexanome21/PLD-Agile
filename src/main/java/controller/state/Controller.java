package controller.state;

import controller.CarteController;
import controller.ServeurHTTP;

public class Controller{
	private State currentState;
	private CarteController carteController;
	private ServeurHTTP serveur;

    // Instances associated with each possible state of the controller
	protected final InitialState initialState = new InitialState();
	protected final CarteChargeState carteChargeState = new CarteChargeState();
	protected final LivraisonChargeState livraisonChargeState = new LivraisonChargeState();
	protected final LivraisonCalculeState livraisonCalculeState = new LivraisonCalculeState();

    /**
	 * Create the controller of the application
	 * 
	 */
	public Controller(CarteController carteController, ServeurHTTP serveur) {
		currentState = initialState;
		this.carteController = carteController;
		this.serveur = serveur;
	}

    /**
	 * Change the current state of the controller
	 * @param state the new current state
	 */
	protected void setCurrentState(State state){
		currentState = state;
	}

	// Methods corresponding to user events 
	/**
	 * Method called after using "chargerCarte"
	 */
	public void chargerCarte() {
		currentState.chargerCarte(this, serveur);
	}

	/**
	 * Method called after using "chargerLivraison"
	 */
	public void chargerLivraison() {
		currentState.chargerLivraison(this, serveur);
	}

    /**
	 * Method called after after using  "CalculerLivraison"
	 */
    public void calculerLivraison() {
        currentState.calculerLivraison(this);
    }

	public CarteController getCarteController() {
        return carteController;
    }
}
