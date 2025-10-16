package app;

import controller.CarteController;
import controller.ServeurHTTP;
import java.io.*;
import model.*;
import tsp.Graphe;
import java.util.*;

public class Main {

    private static final int PORT_SERVEUR = 8000;
    private static final String CHEMIN_BASE_VIEW = "src/main/java/view/";

    public static void main(String[] args) throws IOException {

       // 1. carte
        String cheminFichier = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";
        CarteController carteController = new CarteController();
        carteController.chargerCarteDepuisXML(cheminFichier);
            
        // 2. demandes 
        String cheminDemandes = "ressources/fichiersXMLCollecteDepot/demandePetit1.xml";
        carteController.chargerDemandesDepuisXML(cheminDemandes);

        Carte carte = carteController.getCarte();

            // Afficher les informations de la carte chargée
        System.out.println("Carte chargée avec succès:");
        System.out.println("  - Noeuds: " + carte.getNoeuds().size());
        System.out.println("  - Tronçons: " + carte.getTroncons().size());
        System.out.println();

        Entrepot e = null;
        for (Site site : carte.getSites()) {
            if (site instanceof Entrepot) {
                e = (Entrepot) site;
            }
        }
        GrapheTotal gt = carteController.creerGrapheTotal(carte, e.getId());
        List<Long> cheminMin = carteController.chercherCheminsMin(gt, carte.getSites());
        ServeurHTTP serveur = new ServeurHTTP(PORT_SERVEUR, CHEMIN_BASE_VIEW, carteController);
        serveur.demarrer();

        
        System.out.println("Ouvrez votre navigateur à: http://localhost:" + PORT_SERVEUR);
        System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur");
        System.out.println("===========================================");
    }
}