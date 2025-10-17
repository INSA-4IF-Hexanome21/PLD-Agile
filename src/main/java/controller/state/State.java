package controller.state;

import controller.CarteController;

public interface State {
    /**
	 * Méthode appelée par le controlleur pour charger la carte
	 * @param c le controlleur
	 * @param carteC le controlleur de carte (CarteController)
	 */
	public default void chargerCarte(Controller c, CarteController carteC, String cheminFichier){};
	
	/**
	 * Méthode appelée par le controlleur pour charger une livraison
	 * @param c le controlleur
	 * @param serveur le serveur
	 */
	public default void chargerLivraison(Controller c, CarteController carteC, String cheminFichier){};

	
	/**
	 * Méthode appelée par le controlleur pour calculer une livraison
	 * @param c le controlleur
	 */
	public default void calculerLivraison(Controller c){};
	
	/**
	 * Méthode appelée par le controlleur pour changer l'emplacement d'une livraison
	 * @param c le controlleur
	 */
	public default void changerLivraison(Controller c){};
}
