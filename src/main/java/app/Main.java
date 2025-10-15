package app;

import controller.CarteController;
import controller.ServeurHTTP;
import model.Carte;


import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import controller.CarteController;
import model.Carte;
import java.io.*;
import java.nio.file.Files;

public class Main {

    private static final int PORT_SERVEUR = 8000;
    private static final String CHEMIN_BASE_VIEW = "src/main/java/view/";
    private static final String CHEMIN_BASE_RESSOURCES = "ressources/";


    public static void main(String[] args) throws IOException {

       // 1. carte
        String cheminFichier = "ressources/fichiersXMLPickupDelivery/moyenPlan.xml";
        CarteController carteController = new CarteController();
        carteController.chargerCarteDepuisXML(cheminFichier);
            
        // 2. demandes 
        String cheminDemandes = "ressources/fichiersXMLPickupDelivery/demandeGrand9.xml";
        carteController.chargerDemandesDepuisXML(cheminDemandes);

        Carte carte = carteController.getCarte();

            // Afficher les informations de la carte chargée
        System.out.println("Carte chargée avec succès:");
        System.out.println("  - Nœuds: " + carte.getNoeuds().size());
        System.out.println("  - Tronçons: " + carte.getTroncons().size());
        System.out.println();

        ServeurHTTP serveur = new ServeurHTTP(PORT_SERVEUR, CHEMIN_BASE_VIEW, CHEMIN_BASE_RESSOURCES, carteController);
        serveur.demarrer();

        
       System.out.println("Ouvrez votre navigateur à: http://localhost:" + PORT_SERVEUR);
        System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur");
        System.out.println("===========================================");
    }
}