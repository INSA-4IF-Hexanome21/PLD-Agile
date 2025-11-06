
package controller;
import com.sun.net.httpserver.HttpServer;
import controller.state.Controller;
import utils.ZipUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
     * @param controller Le contrôleur de carte pour l'API
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
                    // Lecture du corps de la requête
                    byte[] bytes = exchange.getRequestBody().readAllBytes();
                    System.out.println("Octets reçus: " + bytes.length);
                    
                    // Obtenir le nom du fichier de l'en-tête
                    String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
                    if (fileName != null) {
                        fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                        System.out.println("Non du fichier: " + fileName);
                    } else {
                        fileName = "plan_" + System.currentTimeMillis() + ".xml";
                        System.out.println("Nombre généré: " + fileName);
                    }
                    
                    // Créer un répertoire s'il n'existe pas
                    File uploadDir = new File(cheminBaseRessources + "uploads/plans/");
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs();
                        System.out.println("Dossier de plans créé: " + created);
                    }
                    
                    // Enregistrer le fichier
                    File outFile = new File(uploadDir, fileName);
                    Files.write(outFile.toPath(), bytes);
                    System.out.println("Plan enregistré dans : " + outFile.getAbsolutePath());
                    
                    // Charger le plan dans le contrôleur
                    System.out.println(">>> Chargement du plan dans le contrôleur <<<");

                    boolean carteChargeReussi = controller.chargerCarte(outFile.getAbsolutePath());
                    if (!carteChargeReussi) {
                        throw new Exception("Le fichier ne permet pas de charger une carte");
                    }
                    
                    // Répondre au client
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
                    System.err.println("ERREUR lors du traitement du plan: " + e.getMessage());
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
                System.out.println("Méthode non permise: " + exchange.getRequestMethod());
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
                // Lecture du corps de la requête
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                System.out.println("Octets reçus: " + bytes.length);
                
                // Obtenir le nom du fichier de l'en-tête
                String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
                if (fileName != null) {
                    fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                    System.out.println("Nombre del archivo: " + fileName);
                } else {
                    fileName = "demande_" + System.currentTimeMillis() + ".xml";
                    System.out.println("Nombre généré: " + fileName);
                }
                
                
                // Créer un répertoire s'il n'existe pas
                File uploadDir = new File(cheminBaseRessources + "uploads/demandes/");
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    System.out.println("Dossier de demandes créé: " + created);
                }
                
                // Enregistrer le fichier
                File outFile = new File(uploadDir, fileName);
                Files.write(outFile.toPath(), bytes);
                System.out.println("Demande enregistrée dans : " + outFile.getAbsolutePath());
                
                // Charger la demande dans le controleur
                System.out.println(">>> Chargement de la demande dans le contrôleur <<<");

                 boolean carteChargeReussi =  controller.chargerLivraison(outFile.getAbsolutePath());
                    if (!carteChargeReussi) {
                        throw new Exception("Le fichier ne permet pas de charger une carte");
                    }
                System.out.println(">>> Demande chargée avec succès <<<");
                
                // Répondre au client
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
                System.err.println("ERREUR lors du traitement de la demande: " + e.getMessage());
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
            System.out.println("Méthode non permise: " + exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    });
        
    /**
     * ! Calculer une livraison (avec les livreurs assignés)
     */
           // Endpoint pour calculer la livraison (demande)
    serveur.createContext("/api/calcul", exchange -> {
        System.out.println(">>> Requête reçue sur /api/calcul <<<");
        
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Lecture du corps de la requête
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                System.out.println("Octets reçus: " + bytes.length);
                
                // Lancer le calcul de la livraison par le contrôleur
                System.out.println(">>> Chargement de la demande dans le contrôleur <<<");
                controller.calculerLivraison();

                System.out.println(">>> Demande chargée avec succès <<<");
                
                // Répondre au client
                String response = "{\"status\":\"ok\",\"type\":\"demande\",\"path\":\"cacul\",\"message\":\"Demande chargée avec succès\"}";
                byte[] responseBytes = response.getBytes("UTF-8");
                
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.close();
                
                System.out.println(">>> Réponse envoyée: " + response + " <<<");
                
            } catch (Exception e) {
                System.err.println("ERREUR lors du traitement du calcul: " + e.getMessage());
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
            System.out.println("Méthode non permise: " + exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    });

    serveur.createContext("/api/assignations", exchange -> {
        System.out.println(">>> Requête reçue sur /api/assignations <<<");

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                String body = new String(bytes, "UTF-8");
                System.out.println("Body reçu: " + body);

                // Parse simple: { "1": ["L001","L003"], "2": ["L002"] }
               HashMap<String, List<String>> assignations = new HashMap<>(); 

                body = body.trim();
                if (body.startsWith("{") && body.endsWith("}")) {
                    body = body.substring(1, body.length() - 1); // SUPRRIMER {}
                    String[] entries = body.split("(?<=\\]),"); // separer keywords

                    for (String entry : entries) {
                        String[] parts = entry.split(":", 2);
                        if (parts.length == 2) {
                            String livreurId = parts[0].trim().replaceAll("\"", "");
                            String values = parts[1].trim();
                            values = values.replaceAll("\\[|\\]|\"", ""); // suprimer [], "
                            List<String> livraisonIds = new ArrayList<>();
                            if (!values.isEmpty()) {
                                for (String id : values.split(",")) {
                                    livraisonIds.add(id.trim());
                                }
                            }
                            assignations.put(livreurId, livraisonIds);
                        }
                    }
                }
                
                 // Vérifier si assignations est vide
                boolean empty = assignations.isEmpty() || assignations.values().stream().allMatch(List::isEmpty);
                if (empty) {
                    String errorResponse = "{\"status\":\"error\",\"message\":\"Aucune assignation fournie\"}";
                    byte[] errorBytes = errorResponse.getBytes("UTF-8");
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(400, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    exchange.close();
                    return;
                }
                // Por ahora solo log
                System.out.println("Assignations reçues: " + assignations);

                // Lancer l'assignation des livraisons par le contrôleur
                System.out.println(">>> Chargement de la demande dans le contrôleur <<<");
                controller.assignerLivreur(assignations);

                // Responder
                String response = "{\"status\":\"ok\",\"message\":\"Assignations reçues\"}";
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.close();

            } catch (Exception e) {
                String errorResponse = "{\"status\":\"error\",\"message\":\"" 
                                        + e.getMessage().replace("\"", "'") + "\"}";
                byte[] errorBytes = errorResponse.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    });



    serveur.createContext("/api/carte", exchange -> {
        System.out.println(">>> Requête reçue sur /api/carte <<<");
        
        String jsonResponse = controller.getCarteJSON();
        byte[] octets = jsonResponse.getBytes("UTF-8");
        
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, octets.length);
        exchange.getResponseBody().write(octets);
        exchange.close();
        
        System.out.println(">>> Réponse envoyée <<<");
    });

    // Endpoint pour télécharger feuille de route
    serveur.createContext("/api/roadmap", exchange -> {
        System.out.println(">>> Requête reçue sur /api/roadmap <<<");

        try {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);
            System.out.println(params);
            controller.genererFeuillesdeRoute();

            if ((params.get("files") != null)
            ) {
                Path dossier = Paths.get(cheminBaseRessources + "downloads/");
                if (!Files.exists(dossier)) {
                    Files.createDirectories(dossier);
                    System.out.println("Dossier 'downloads' créé !");
                } 
                Map<String, byte[]> fichiers = new HashMap<String, byte[]>();
                Files.list(dossier)
                    .forEach(f -> {
                        try {
                            String nomFichier = f.getFileName().toString();
                            byte[] contenu = Files.readAllBytes(f);
                            if (params.get("files").contains(f.getFileName().toString())) {
                                fichiers.put(nomFichier, contenu);
                            } 
                        } catch (Exception e) {
                            System.err.println("ERREUR lors du téléchargement des fichiers: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                });
                String nomZipFic = cheminBaseRessources+"downloads/roadmap.zip";
                ZipUtils.createZip(fichiers, nomZipFic); 
                File zipFic = new File(nomZipFic);
                byte[] octets = Files.readAllBytes(zipFic.toPath());

                exchange.getResponseHeaders().set("Content-Type", "application/zip");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"roadmap.zip\"");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                exchange.sendResponseHeaders(200, octets.length);
                exchange.getResponseBody().write(octets);

            } else if (params.get("file") != null) {
                String nomFichier = params.get("file");
                File fichier = new File(cheminBaseRessources + "downloads/" + nomFichier);
                byte[] octets = Files.readAllBytes(fichier.toPath());
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"roadmap.txt\"");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                exchange.sendResponseHeaders(200, octets.length);
                exchange.getResponseBody().write(octets);

            }

        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            
            exchange.close();
            System.out.println(">>> Réponse envoyée <<<");
        }
    });

        //  Route pour les fichiers JavaScript
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
        
        //  Route pour les fichiers CSS
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

        serveur.createContext("/api/undoAction", exchange -> {
            
            try {
                System.out.println(">>> Requête reçue sur /api/undoAction <<<");
                
                exchange.getRequestBody().readAllBytes();

                controller.undoAction();

                byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.getResponseBody().close();
                
                System.out.println(">>> Réponse envoyée <<<");
                
            } catch (Exception e) {
                System.err.println("!!! ERREUR dans /api/undoAction !!!");
                e.printStackTrace();
                
                try {
                    String errorMsg = "{\"error\": \"" + e.getMessage() + "\"}";
                    byte[] errorResponse = errorMsg.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, errorResponse.length);
                    exchange.getResponseBody().write(errorResponse);
                    exchange.getResponseBody().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } finally {
                exchange.close();
            }
        });

        serveur.createContext("/api/redoAction", exchange -> {
            
            try {
                System.out.println(">>> Requête reçue sur /api/redoAction <<<");
                
                exchange.getRequestBody().readAllBytes();

                controller.redoAction();

                byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.getResponseBody().close();
                
                System.out.println(">>> Réponse envoyée <<<");
                
            } catch (Exception e) {
                System.err.println("!!! ERREUR dans /api/redoAction !!!");
                e.printStackTrace();
                
                try {
                    String errorMsg = "{\"error\": \"" + e.getMessage() + "\"}";
                    byte[] errorResponse = errorMsg.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, errorResponse.length);
                    exchange.getResponseBody().write(errorResponse);
                    exchange.getResponseBody().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } finally {
                exchange.close();
            }
        });

        serveur.createContext("/api/resetCarte", exchange -> {
            
            System.out.println(">>> Requête reçue sur /api/resetCarte <<<");
        
            controller.resetCarte();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            
        });

        serveur.createContext("/api/deleteSite", exchange -> {
            System.out.println(">>> Requête reçue sur /api/deleteSite <<<");

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                String body = new String(bytes, "UTF-8");
                System.out.println("Body reçu: " + body);
                // Parse simple: { "idSite": 1243543253, "typeSite": "collecte", "numLivraison": 3 }
                Map<String, String> params = new HashMap<>();
                
                body = body.trim()
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "");

                // split sur chaque champ
                String[] pairs = body.split(",");

                for (String p : pairs) {
                    String[] kv = p.split(":");
                    if (kv.length == 2) {
                        params.put(kv[0].trim(), kv[1].trim());
                    }
                }
                Long idSite = Long.valueOf(params.get("idSite"));
                String typeSite = params.get("typeSite").toString();
                Integer numLivraison = Integer.valueOf(params.get("numLivraison"));

                // Por ahora solo log
                System.out.println("idSite=" + idSite+", typeSite=" + typeSite+ ", numLivraison=" + numLivraison);

                // Lancer la suppression du site par le contrôleur
                System.out.println(">>> Chargement de la demande dans le contrôleur <<<");
                controller.supprimerLivraison(idSite, typeSite, numLivraison);;

                // Responder
                String response = "{\"status\":\"ok\",\"message\":\"Suppression reçue\"}";
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.close(); 

            } catch (Exception e) {
                String errorResponse = "{\"status\":\"error\",\"message\":\"" 
                                        + e.getMessage().replace("\"", "'") + "\"}";
                byte[] errorBytes = errorResponse.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
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

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2)
                map.put(pair[0], pair[1]);
        }
        return map;
    }
}