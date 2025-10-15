package controller;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * Serveur HTTP pour servir les fichiers statiques et l'API
 * 
 * @author aza
 */
public class ServeurHTTP {
    
    private final HttpServer serveur;
    private final String cheminBase;
    private final String cheminBaseRessources;
    private final CarteController carteController;
    
    /**
     * Constructeur du serveur HTTP
     * 
     * @param port Le port sur lequel le serveur écoute
     * @param cheminBase Le chemin de base des fichiers à servir
     * @param carteController Le contrôleur de carte pour l'API
     * @throws IOException Si le serveur ne peut pas être créé
     */
    public ServeurHTTP(int port, String cheminBase, String cheminBaseRessources, CarteController carteController) throws IOException {
        this.serveur = HttpServer.create(new InetSocketAddress(port), 0);
        this.cheminBase = cheminBase;
        this.cheminBaseRessources = cheminBaseRessources;
        this.carteController = carteController;
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
        
       serveur.createContext("/api/carte", exchange -> {
        System.out.println(">>> Requête reçue sur /api/carte <<<");
        
        String jsonResponse = carteController.getCarteJSON();
        byte[] octets = jsonResponse.getBytes("UTF-8");
        
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, octets.length);
        exchange.getResponseBody().write(octets);
        exchange.close();
        
        System.out.println(">>> Réponse envoyée <<<");
    });
        

        serveur.createContext("/api/upload", exchange -> {
            System.out.println(">>> Requête reçue sur /api/upload <<<");
            
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    // Leer el cuerpo de la petición
                    byte[] bytes = exchange.getRequestBody().readAllBytes();
                    System.out.println("Bytes recibidos: " + bytes.length);
                    
                    // Obtener el nombre del archivo del header
                    String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
                    if (fileName != null) {
                        fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                        System.out.println("Nombre del archivo: " + fileName);
                    } else {
                        fileName = "archivo_" + System.currentTimeMillis();
                        System.out.println("Nombre generado: " + fileName);
                    }
                    
                    // Crear directorio si no existe
                    File uploadDir = new File(cheminBaseRessources + "uploads/");
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs();
                        System.out.println("Directorio de uploads creado: " + created);
                    }
                    
                    // Guardar el archivo
                    File outFile = new File(uploadDir, fileName);
                    Files.write(outFile.toPath(), bytes);
                    System.out.println("Archivo guardado en: " + outFile.getAbsolutePath());
                    
                    // Responder al cliente
                    String response = "{\"status\":\"ok\",\"path\":\"uploads/" + fileName + "\",\"size\":" + bytes.length + "}";
                    byte[] responseBytes = response.getBytes("UTF-8");
                    
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    exchange.getResponseBody().write(responseBytes);
                    exchange.close();
                    
                    System.out.println(">>> Réponse envoyée: " + response + " <<<");
                    
                } catch (Exception e) {
                    System.err.println("ERROR al procesar upload: " + e.getMessage());
                    e.printStackTrace();
                    
                    String errorResponse = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
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


        // Route pour les fichiers JavaScript
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
        
        // Route pour les fichiers CSS
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
        
        // Route pour les images (logos, favicon, etc.)
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