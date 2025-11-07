package controller.state;

import java.util.HashMap;
import java.util.List;

import controller.CarteController;

public interface State {
    /**
     * Méthode appelée par le controlleur pour charger la carte
     * @param c le controlleur
     * @param carteC le controlleur de carte (CarteController)
     * @param cheminFichier le chemin du fichier XML de la carte
     */
    public default boolean chargerCarte(Controller c, CarteController carteC, String cheminFichier) {
        return false;
    }
    
    /**
     * Méthode appelée par le controlleur pour charger une livraison
     * @param c le controlleur
     * @param carteC le controlleur de carte (CarteController)
     * @param cheminFichier le chemin du fichier XML de la livraison
     */
    public default boolean chargerLivraison(Controller c, CarteController carteC, String cheminFichier) {
        return false;
    }

    /**
     * Méthode appelée par le controlleur pour calculer une livraison
     * @param c le controlleur
     */
    public default void calculerLivraison(Controller c, CarteController carteC) {}
    
    /**
     * Méthode appelée par le controlleur pour changer l'emplacement d'une livraison
     * @param c le controlleur
     */
    public default void assignerLivreur(Controller c, CarteController carteC, HashMap<String, List<String>> assignations) {}

    public default void ajouterLivraison(Controller c, CarteController carteC, String idCollecte,String idPrecCollecte,String idDepot,String idPrecDepot,String numTrajet) {}

    /**
     * Méthode appelée par le controlleur pour changer l'emplacement d'une livraison
     * @param c le controlleur
     */
    public default void changerLivraison(Controller c) {}

    /**
     * Méthode appelée par le controlleur pour changer l'emplacement d'une livraison
     * @param c le controlleur
     */
    public default void genererFeuillesdeRoute(Controller c, CarteController carteC) {}

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