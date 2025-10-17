package controller;

public interface State {
    /**
	 * Method called by the controller after using "ChargerCarte"
	 * @param c the controler
	 */
	public default void chargerCarte(Controller c, ServeurHTTP serveur){};
	
	/**
	 * Method called by the controller after using "Chargerlivraison"
	 * @param c the controler
	 */
	public default void chargerLivraison(Controller c, ServeurHTTP serveur){};

	
	/**
	 * Method called by the controller after using  "CalculerLivraison"
	 * @param c the controler
	 */
	public default void calculerLivraison(Controller c){};
	
}
