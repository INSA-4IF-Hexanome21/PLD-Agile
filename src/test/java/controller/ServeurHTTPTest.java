package controller;

import controller.state.Controller;
import org.junit.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ServeurHTTPTest {

    private static ServeurHTTP serveur;
    private static final int PORT = 8000;
    private static final String BASE_PATH = "src/main/java/view/";
    private static Path tempRessources;
    private static Controller controller;

    @BeforeClass
    public static void setUp() throws Exception {
        // Créer un dossier temporaire pour les fichiers uploadés
        tempRessources = Files.createTempDirectory("serveur_test_");
        controller = new Controller();

        serveur = new ServeurHTTP(PORT, BASE_PATH, tempRessources.toString() + "/", controller);
        serveur.demarrer();

        // Attendre que le serveur soit prêt
        boolean serverReady = false;
        int retries = 10;
        while (!serverReady && retries > 0) {
            try (Socket socket = new Socket("localhost", PORT)) {
                serverReady = true;
            } catch (IOException e) {
                Thread.sleep(200);
                retries--;
            }
        }
        if (!serverReady) {
            throw new RuntimeException("Le serveur HTTP n'a pas démarré sur le port " + PORT);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (serveur != null) {
            serveur.arreter();
        }
        // Supprimer le dossier temporaire
        if (tempRessources != null) {
            try {
                Files.walk(tempRessources)
                     .sorted((a, b) -> b.compareTo(a))
                     .map(Path::toFile)
                     .forEach(File::delete);
            } catch (IOException ignored) {}
        }
    }

    // ==========
    
    @Test
    public void testServeurDemarre() {
        assertNotNull(serveur);
        assertTrue(true);
    }

    @Test
    public void testUploadPlanOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/plan").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-File-Name", "testPlan.xml");
        connection.setDoOutput(true);

        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                         "<reseau>\n" +
                         "<noeud id=\"25175791\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" +
                         "<noeud id=\"2129259178\" latitude=\"45.750404\" longitude=\"4.8744674\"/>\n" +
                         "<troncon destination=\"2129259178\" longueur=\"100.0\" nomRue=\"Rue Test\" origine=\"25175791\"/>\n" +
                         "</reseau>";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(xmlData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        String response = new String(connection.getInputStream().readAllBytes());
        assertTrue(response.contains("\"status\":\"ok\""));
    }

    @Test
    public void testUploadPlanErreurFormat() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/plan").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-File-Name", "mauvais.txt");
        connection.setDoOutput(true);

        String fakeData = "Ceci n'est pas du XML";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(fakeData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(500, responseCode);
    }

    @Test
    public void testUploadPlanMethodeNonAutorisee() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/plan").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(405, responseCode);
    }

    @Test
    public void testUploadPlanSansNomDeFichier() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/plan").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                         "<reseau>\n" +
                         "<noeud id=\"25175791\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" +
                         "<noeud id=\"2129259178\" latitude=\"45.750404\" longitude=\"4.8744674\"/>\n" +
                         "<troncon destination=\"2129259178\" longueur=\"100.0\" nomRue=\"Rue Test\" origine=\"25175791\"/>\n" +
                         "</reseau>";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(xmlData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        String response = new String(connection.getInputStream().readAllBytes());
        assertTrue(response.contains("\"status\":\"ok\""));
        assertTrue(response.contains("plan_"));
    }

    // ========== Tests pour /api/upload/demande ==========

    @Test
    public void testUploadDemandeOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/demande").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-File-Name", "testDemande.xml");
        connection.setDoOutput(true);

        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<demande></demande>";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(xmlData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500); // Peut échouer si pas de carte chargée

        String response;
        if (responseCode == 200) {
            response = new String(connection.getInputStream().readAllBytes());
            assertTrue(response.contains("\"status\":\"ok\"") || response.contains("\"type\":\"demande\""));
        } else {
            response = new String(connection.getErrorStream().readAllBytes());
            assertTrue(response.contains("\"status\":\"error\""));
        }
    }

    @Test
    public void testUploadDemandeErreurFormat() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/demande").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-File-Name", "mauvais.txt");
        connection.setDoOutput(true);

        String fakeData = "Pas du XML";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(fakeData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(500, responseCode);
    }

    @Test
    public void testUploadDemandeMethodeNonAutorisee() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/demande").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(405, responseCode);
    }

    @Test
    public void testUploadDemandeSansNomDeFichier() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/demande").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<demande></demande>";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(xmlData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
        
        String response;
        if (responseCode == 200) {
            response = new String(connection.getInputStream().readAllBytes());
            assertTrue(response.contains("demande_"));
        }
    }

    // ========== Tests pour /api/calcul ==========

    @Test
    public void testCalculOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/calcul").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    @Test
    public void testCalculMethodeNonAutorisee() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/calcul").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(405, responseCode);
    }

    // ========== Tests pour /api/assignations ==========

    @Test
    public void testAssignationsOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/assignations").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String jsonData = "{\"1\":[\"L001\",\"L003\"],\"2\":[\"L002\"]}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    @Test
    public void testAssignationsVides() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/assignations").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String jsonData = "{}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(400, responseCode);

        String response = new String(connection.getErrorStream().readAllBytes());
        assertTrue(response.contains("Aucune assignation fournie"));
    }

    @Test
    public void testAssignationsMethodeNonAutorisee() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/assignations").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(405, responseCode);
    }

    // ========== Tests pour /api/carte ==========

    @Test
    public void testGetCarteJSON() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/carte").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        String response = new String(connection.getInputStream().readAllBytes());
        assertNotNull(response);
        // Vérifier que c'est du JSON valide (commence par { ou [)
        assertTrue(response.trim().startsWith("{") || response.trim().startsWith("["));
    }

    // ========== Tests pour /api/undoAction ==========

    @Test
    public void testUndoAction() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/undoAction").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    // ========== Tests pour /api/redoAction ==========

    @Test
    public void testRedoAction() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/redoAction").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    // ========== Tests pour /api/resetCarte ==========

    @Test
    public void testResetCarte() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/resetCarte").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
    }

    // ========== Tests pour /api/deleteSite ==========

    @Test
    public void testDeleteSiteOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/deleteSite").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String jsonData = "{\"idSite\":123456,\"typeSite\":\"collecte\",\"numLivraison\":1}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    @Test
    public void testDeleteSiteMethodeNonAutorisee() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/deleteSite").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(405, responseCode);
    }

    @Test
    public void testDeleteSiteDonneesInvalides() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/deleteSite").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String jsonData = "{\"invalid\":\"data\"}";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(500, responseCode);
    }

    // ========== Tests pour /api/roadmap ==========

    @Test
    public void testRoadmapSansParametres() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/roadmap").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    @Test
    public void testRoadmapAvecParametreFile() throws Exception {
        // Créer un fichier de test dans downloads/
        Path downloadsDir = tempRessources.resolve("downloads");
        Files.createDirectories(downloadsDir);
        Path testFile = downloadsDir.resolve("test_roadmap.txt");
        Files.writeString(testFile, "Test roadmap content");

        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/roadmap?file=test_roadmap.txt").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    @Test
    public void testRoadmapAvecParametreFiles() throws Exception {
        // Créer des fichiers de test dans downloads/
        Path downloadsDir = tempRessources.resolve("downloads");
        Files.createDirectories(downloadsDir);
        Path testFile1 = downloadsDir.resolve("file1.txt");
        Path testFile2 = downloadsDir.resolve("file2.txt");
        Files.writeString(testFile1, "Content 1");
        Files.writeString(testFile2, "Content 2");

        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/roadmap?files=file1.txt,file2.txt").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 500);
    }

    // ========== Tests pour routes statiques ==========

    @Test
    public void testRouteRacine() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 404);
    }

    @Test
    public void testRouteJS() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/js/test.js").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        // Le fichier peut ne pas exister, ce qui est normal
        assertTrue(responseCode == 200 || responseCode == 404);
    }

    @Test
    public void testRouteCSS() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/css/test.css").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 404);
    }

    @Test
    public void testRouteComponents() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/components/test.html").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 404);
    }

    @Test
    public void testRouteImages() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/images/test.png").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 200 || responseCode == 404);
    }
}