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

        serveur = new ServeurHTTP(PORT, BASE_PATH, tempRessources.toString(), controller);
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
                     .map(Path::toFile)
                     .forEach(File::delete);
            } catch (IOException ignored) {}
        }
    }

    @Test
    public void testServeurDemarre() {
        assertNotNull(serveur);
        // On teste juste que le serveur est bien démarré
        assertTrue(true);
    }

    @Test
    public void testUploadPlanOk() throws Exception {
        URL url = java.net.URI.create("http://localhost:" + PORT + "/api/upload/plan").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-File-Name", "testPlan.xml");
        connection.setDoOutput(true);

        String xmlData = "<carte></carte>";
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

        String xmlData = "<carte></carte>";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(xmlData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        String response = new String(connection.getInputStream().readAllBytes());
        assertTrue(response.contains("\"status\":\"ok\""));
        assertTrue(response.contains("plan_"));
    }
}
