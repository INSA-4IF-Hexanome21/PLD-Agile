package app;

import controller.CarteController;
import controller.ServeurHTTP;
import controller.state.Controller;

import java.io.*;

public class Main {

    private static final int PORT_SERVEUR = 8000;
    private static final String CHEMIN_BASE_VIEW = "src/main/java/view/";
    private static final String CHEMIN_BASE_RESSOURCES = "ressources/";


    public static void main(String[] args) throws IOException {

       // 1. carte
        
        CarteController carteController = new CarteController();
        Controller controller = new Controller(carteController);

        ServeurHTTP serveur = new ServeurHTTP(PORT_SERVEUR, CHEMIN_BASE_VIEW, CHEMIN_BASE_RESSOURCES, controller);
        serveur.demarrer();


        
       System.out.println("Ouvrez votre navigateur à: http://localhost:" + PORT_SERVEUR);
        System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur");
        System.out.println("===========================================");
    }
}