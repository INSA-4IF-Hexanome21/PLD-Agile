package controller.state;


import java.util.HashMap;
import java.util.List;

import controller.CarteController;


public class Controller{
	private State currentState;
	private CarteController carteController;
	

     // Instances associées avec chaque état possible du controlleur 
	protected final InitialState initialState = new InitialState();
	protected final CarteChargeState carteChargeState = new CarteChargeState();
	protected final LivraisonChargeState livraisonChargeState = new LivraisonChargeState();
	protected final LivreurAssigneState livreurAssigneState = new LivreurAssigneState();
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
	 * Getter pour l'état courant
	 */
	public State getCurrentState() {
		return currentState;
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
	 * Méthode pour assigner les livraisons aux livreurs
	 * VerifAssignation : temp, juste pour pouvoir tester une fois (et une seule avant de devoir recompiler)
	 */
	public void assignerLivreur(HashMap<String, List<String>> assignations){
			currentState.assignerLivreur(this, carteController, assignations);
	}

	/**
	 * Méthode pour générer les feuilles de routes
	 */
    public void genererFeuillesdeRoute() {
        currentState.genererFeuillesdeRoute(this, carteController);
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

	public String undoAction() {
		carteController.undo();
		return getCarteJSON();
	}

	public String redoAction() {
		carteController.redo();
		return getCarteJSON();
	}

	public void resetCarte() {
		carteController.resetCarte();
	}
}
