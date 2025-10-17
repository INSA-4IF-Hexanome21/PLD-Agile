package controller.state;

import controller.ServeurHTTP;

public interface State {
    /**
	 * Méthode appelée par le controlleur pour charger la carte
	 * @param c le controlleur
	 * @param serveur le serveur
	 */
	public default void chargerCarte(Controller c, ServeurHTTP serveur){};
	
	/**
	 * Méthode appelée par le controlleur pour charger une livraison
	 * @param c le controlleur
	 * @param serveur le serveur
	 */
	public default void chargerLivraison(Controller c, ServeurHTTP serveur){};

	
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
