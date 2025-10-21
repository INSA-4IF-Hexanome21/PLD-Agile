package controller.state;

import controller.CarteController;

public interface State {
    /**
     * Méthode appelée par le controlleur pour charger la carte
     * @param c le controlleur
     * @param carteC le controlleur de carte (CarteController)
     * @param cheminFichier le chemin du fichier XML de la carte
     */
    public default void chargerCarte(Controller c, CarteController carteC, String cheminFichier) {}
    
    /**
     * Méthode appelée par le controlleur pour charger une livraison
     * @param c le controlleur
     * @param carteC le controlleur de carte (CarteController)
     * @param cheminFichier le chemin du fichier XML de la livraison
     */
    public default void chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {}

    /**
     * Méthode appelée par le controlleur pour calculer une livraison
     * @param c le controlleur
     */
    public default void calculerLivraison(Controller c) {}
    
    /**
     * Méthode appelée par le controlleur pour changer l'emplacement d'une livraison
     * @param c le controlleur
     */
    public default void changerLivraison(Controller c) {}

    /**
     * Méthode pour obtenir le JSON de la carte
     * @param c le controlleur
     * @param carteC le controlleur de carte
     * @return le JSON de la carte
     */
    public default String getCarteJSON(Controller c, CarteController carteC) {
        return carteC.getCarteJSON();
    }
}