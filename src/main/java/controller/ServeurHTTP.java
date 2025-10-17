
package controller;
import com.sun.net.httpserver.HttpServer;

import controller.state.Controller;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import model.Carte;

/**
 * Serveur HTTP pour servir les fichiers statiques et l'API
 * 
 * @author aza
 */
public class ServeurHTTP {
    
    private final HttpServer serveur;
    private final String cheminBase;
    private final String cheminBaseRessources;
    private final Controller controller;

    /**
     * Constructeur du serveur HTTP
     * 
     * @param port Le port sur lequel le serveur écoute
     * @param cheminBase Le chemin de base des fichiers à servir
     * @para Le contrôleur de carte pour l'API
     * @throws IOException Si le serveur ne peut pas être créé
     */
    public ServeurHTTP(int port, String cheminBase, String cheminBaseRessources, Controller controller) throws IOException {
        this.serveur = HttpServer.create(new InetSocketAddress(port), 0);
        this.cheminBase = cheminBase;
        this.cheminBaseRessources = cheminBaseRessources;
        this.controller = controller;
        configurerRoutes();
    }
    
    /**
     * Configure toutes les routes du serveur
     */
    private void configurerRoutes() {
        // Route principale - servir Index.html
        serveur.createContext("/", exchange -> {
            File fichier = new File(cheminBase + "Index.html");
            if (!fichier.exists()) {
                String messageErreur = "Fichier non trouvé: " + fichier.getAbsolutePath();
                System.out.println(messageErreur);
                exchange.sendResponseHeaders(404, messageErreur.length());
                exchange.getResponseBody().write(messageErreur.getBytes());
                exchange.close();
                return;
            }
            byte[] octets = Files.readAllBytes(fichier.toPath());
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, octets.length);
            exchange.getResponseBody().write(octets);
            exchange.close();
        });

    /**
     * ! Charger une carte
    */

      // Endpoint pour charger la carte (plan)
        serveur.createContext("/api/upload/plan", exchange -> {
            System.out.println(">>> Requête reçue sur /api/upload/plan <<<");
            
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    // Lecture du coprs de la requête
                    byte[] bytes = exchange.getRequestBody().readAllBytes();
                    System.out.println("Bytes recibidos: " + bytes.length);
                    
                    // Obtenir le nom du fichier de l'en-tête
                    String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
                    if (fileName != null) {
                        fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                        System.out.println("Nombre del archivo: " + fileName);
                    } else {
                        fileName = "plan_" + System.currentTimeMillis() + ".xml";
                        System.out.println("Nombre generado: " + fileName);
                    }
                    
                    // Vérifier qu'il s'agit d'une fichier XML
                    if (!fileName.toLowerCase().endsWith(".xml")) {
                        throw new Exception("Le fichier doit être un XML");
                    }
                    
                    // Créer un répertoire s'il n'existe pas
                    File uploadDir = new File(cheminBaseRessources + "uploads/plans/");
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs();
                        System.out.println("Directorio de plans creado: " + created);
                    }
                    
                    // Enregistrer le fichier
                    File outFile = new File(uploadDir, fileName);
                    Files.write(outFile.toPath(), bytes);
                    System.out.println("Plan guardado en: " + outFile.getAbsolutePath());
                    
                    // Charger le plan dans le controleur
                    System.out.println(">>> Chargement du plan dans le contrôleur <<<");

                    controller.chargerCarte(outFile.getAbsolutePath());

                    // carteController.chargerCarteDepuisXML(outFile.getAbsolutePath());
                    // System.out.println(">>> Plan chargé avec succès <<<");
                    
                    // Rrépondre au client
                    String response = "{\"status\":\"ok\",\"type\":\"plan\",\"path\":\"uploads/plans/" 
                                    + fileName + "\",\"size\":" + bytes.length + ",\"message\":\"Plan chargé avec succès\"}";
                    byte[] responseBytes = response.getBytes("UTF-8");
                    
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    exchange.getResponseBody().write(responseBytes);
                    exchange.close();
                    
                    System.out.println(">>> Réponse envoyée: " + response + " <<<");
                    
                } catch (Exception e) {
                    System.err.println("ERROR al procesar plan: " + e.getMessage());
                    e.printStackTrace();
                    
                    String errorResponse = "{\"status\":\"error\",\"type\":\"plan\",\"message\":\"" 
                                        + e.getMessage().replace("\"", "'") + "\"}";
                    byte[] errorBytes = errorResponse.getBytes("UTF-8");
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    exchange.close();
                }
            } else {
                System.out.println("Método no permitido: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });


    /**
     * ! Charger une livraison
     */
             // Endpoint pour charger la livraison (demande)
    serveur.createContext("/api/upload/demande", exchange -> {
        System.out.println(">>> Requête reçue sur /api/upload/demande <<<");
        
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Lecture du coprs de la requête
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                System.out.println("Bytes recibidos: " + bytes.length);
                
                // Obtener el nombre del archivo del header
                String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
                if (fileName != null) {
                    fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                    System.out.println("Nombre del archivo: " + fileName);
                } else {
                    fileName = "demande_" + System.currentTimeMillis() + ".xml";
                    System.out.println("Nombre generado: " + fileName);
                }
                
                // Validar que sea XML
                if (!fileName.toLowerCase().endsWith(".xml")) {
                    throw new Exception("Le fichier doit être un XML");
                }
                
                // Crear directorio si no existe
                File uploadDir = new File(cheminBaseRessources + "uploads/demandes/");
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    System.out.println("Directorio de demandes creado: " + created);
                }
                
                // Guardar el archivo
                File outFile = new File(uploadDir, fileName);
                Files.write(outFile.toPath(), bytes);
                System.out.println("Demande guardado en: " + outFile.getAbsolutePath());
                
                // CARGAR LA DEMANDE EN EL CONTROLADOR
                // Asumiendo que tienes un método para cargar demandes
                System.out.println(">>> Chargement de la demande dans le contrôleur <<<");

                controller.chargerLivraison();

                // carteController.chargerDemandesDepuisXML(outFile.getAbsolutePath());
                // System.out.println(">>> Demande chargée avec succès <<<");
                
                // Responder al cliente
                String response = "{\"status\":\"ok\",\"type\":\"demande\",\"path\":\"uploads/demandes/" 
                                + fileName + "\",\"size\":" + bytes.length + ",\"message\":\"Demande chargée avec succès\"}";
                byte[] responseBytes = response.getBytes("UTF-8");
                
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.close();
                
                System.out.println(">>> Réponse envoyée: " + response + " <<<");
                
            } catch (Exception e) {
                System.err.println("ERROR al procesar demande: " + e.getMessage());
                e.printStackTrace();
                
                String errorResponse = "{\"status\":\"error\",\"type\":\"demande\",\"message\":\"" 
                                    + e.getMessage().replace("\"", "'") + "\"}";
                byte[] errorBytes = errorResponse.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.close();
            }
        } else {
            System.out.println("Método no permitido: " + exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    });
        
    //    serveur.createContext("/api/carte", exchange -> {
    //     System.out.println(">>> Requête reçue sur /api/carte <<<");
        
    //     //   String jsonResponse = controller.getCarteJSON();
    //     String jsonResponse = carteController.getCarteJSON();
    //     byte[] octets = jsonResponse.getBytes("UTF-8");
        
    //     exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    //     exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    //     exchange.sendResponseHeaders(200, octets.length);
    //     exchange.getResponseBody().write(octets);
    //     exchange.close();
        
    //     System.out.println(">>> Réponse envoyée <<<");
    // });
        // ! Route pour les fichiers JavaScript
        serveur.createContext("/js/", exchange -> {
            String chemin = exchange.getRequestURI().getPath().replaceFirst("/js/", "");
            File fichier = new File(cheminBase + "JS/" + chemin);
            if (!fichier.exists()) {
                System.out.println("JS non trouvé: " + fichier.getAbsolutePath());
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] octets = Files.readAllBytes(fichier.toPath());
            exchange.getResponseHeaders().add("Content-Type", "application/javascript; charset=UTF-8");
            exchange.sendResponseHeaders(200, octets.length);
            exchange.getResponseBody().write(octets);
            exchange.close();
        });
        
        // ! Route pour les fichiers CSS
        serveur.createContext("/css/", exchange -> {
            String chemin = exchange.getRequestURI().getPath().replaceFirst("/css/", "");
            File fichier = new File(cheminBase + "CSS/" + chemin);
            if (!fichier.exists()) {
                System.out.println("CSS non trouvé: " + fichier.getAbsolutePath());
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] octets = Files.readAllBytes(fichier.toPath());
            exchange.getResponseHeaders().add("Content-Type", "text/css; charset=UTF-8");
            exchange.sendResponseHeaders(200, octets.length);
            exchange.getResponseBody().write(octets);
            exchange.close();
        });
        
        // Route pour les composants HTML
        serveur.createContext("/components/", exchange -> {
            String chemin = exchange.getRequestURI().getPath().replaceFirst("/components/", "");
            File fichier = new File(cheminBase + "components/" + chemin);
            if (!fichier.exists()) {
                System.out.println("Composant non trouvé: " + fichier.getAbsolutePath());
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] octets = Files.readAllBytes(fichier.toPath());
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, octets.length);
            exchange.getResponseBody().write(octets);
            exchange.close();
        });
        
        // ! Route pour les images (logos, favicon, etc.)
        serveur.createContext("/images/", exchange -> {
            String chemin = exchange.getRequestURI().getPath().replaceFirst("/images/", "");
            File fichier = new File(cheminBase + "images/" + chemin);
            if (!fichier.exists()) {
                System.out.println("Image non trouvée: " + fichier.getAbsolutePath());
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] octets = Files.readAllBytes(fichier.toPath());
            
            // Déterminer le type MIME selon l'extension
            String contentType = "image/png";
            if (chemin.endsWith(".jpg") || chemin.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (chemin.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else if (chemin.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (chemin.endsWith(".ico")) {
                contentType = "image/x-icon";
            }
            
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, octets.length);
            exchange.getResponseBody().write(octets);
            exchange.close();
        });
    }
    


    /**
     * Démarre le serveur HTTP
     */
    public void demarrer() {
        serveur.start();
        System.out.println("===========================================");
        System.out.println("Serveur démarré sur http://localhost:" + serveur.getAddress().getPort());
        System.out.println("===========================================");
    }
    
    /**
     * Arrête le serveur HTTP
     */
    public void arreter() {
        serveur.stop(0);
        System.out.println("Serveur arrêté");
    }
}