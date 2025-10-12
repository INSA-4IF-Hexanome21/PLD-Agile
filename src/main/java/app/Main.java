package app;

import controller.CarteController;
import model.Carte;

public class Main {
    public static void main(String[] args) {
       // Chemin vers ton fichier XML local
        String cheminFichier = "ressources/fichiersXMLPickupDelivery/grandPlan.xml";
        CarteController carteController = new CarteController(cheminFichier);
        carteController.chargerCarteDepuisXML();
        Carte carte = carteController.getCarte();
        System.out.println("Nombre de noeuds dans la carte : " + carte.getNoeuds().size());
        System.out.println("Nombre de tronçons dans la carte : " + carte.getTroncons().size());
    }
}