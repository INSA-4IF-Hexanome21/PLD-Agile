package app;

import controller.CarteController;
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
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

       // Chemin vers ton fichier XML local
        String cheminFichier = "ressources/fichiersXMLPickupDelivery/grandPlan.xml";
        CarteController carteController = new CarteController(cheminFichier);
        carteController.chargerCarteDepuisXML();

        Carte carte = carteController.getCarte();

        // Servir HTML
        server.createContext("/", exchange -> {
        InputStream htmlStream = Main.class.getClassLoader().getResourceAsStream("view/PickupDelivery.html");
        byte[] bytes = htmlStream.readAllBytes();

            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        // Servir JSON
        server.createContext("/api/carte", exchange -> {
            byte[] bytes = carteController.getCarteJSON().getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        server.start();
        System.out.println("Serveur http://localhost:8000");

        
        System.out.println("Nombre de noeuds dans la carte : " + carte.getNoeuds().size());
        System.out.println("Nombre de tronçons dans la carte : " + carte.getTroncons().size());
    }
}