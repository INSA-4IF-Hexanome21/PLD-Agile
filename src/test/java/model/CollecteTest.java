package model;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

public class CollecteTest {

    @Test
    public void getters_fonctionnentCorrectement() {
        // Test du constructeur complet
        Collecte c = new Collecte(1L, 45.0f, 4.0f, 5, 120);

        // Vérifie les getters
        assertEquals(Long.valueOf(1L), Long.valueOf(c.getId()));
        assertEquals(Float.valueOf(45.0f), Float.valueOf(c.getLatitude()));
        assertEquals(Float.valueOf(4.0f), Float.valueOf(c.getLongitude()));
        assertEquals(Integer.valueOf(5), c.getNumLivraison());
        assertEquals(Integer.valueOf(120), c.getDureeRecup());
    }

    @Test
    public void setters_fonctionnentCorrectement() {
        // Test du constructeur minimal
        Collecte c = new Collecte(2L, null, null, 10, 200);

        // Modification des valeurs via les setters
        c.setNumLivraison(15);
        c.setDureeRecup(300);

        // Vérification des changements
        assertEquals(Integer.valueOf(15), c.getNumLivraison());
        assertEquals(Integer.valueOf(300), c.getDureeRecup());
    }

    @Test
    public void getTypeSite_retourneCollecte() {
        Collecte c = new Collecte(3L, 1, 60);
        assertEquals("collecte", c.getTypeSite());
    }

    @Test
    public void heures_fonctionnentCorrectement() {
        Collecte c = new Collecte(4L, 10.0f, 20.0f, 2, 60);

        // Test des setters hérités de Site
        LocalTime depart = LocalTime.of(8, 30);
        LocalTime arrivee = LocalTime.of(9, 0);

        c.setDepartHeure(depart);
        c.setArriveeHeure(arrivee);

        // Vérification des getters
        assertEquals(depart, c.getDepartHeure());
        assertEquals(arrivee, c.getArriveeHeure());
    }

    @Test
    public void constructeurMinimal_fonctionneCorrectement() {
        Collecte c = new Collecte(5L, null, null, 0, 0);

        // Vérification des valeurs initiales
        assertEquals(Long.valueOf(5L), Long.valueOf(c.getId()));
        assertNull(c.getLatitude());
        assertNull(c.getLongitude());
        assertEquals(Integer.valueOf(0), c.getNumLivraison());
        assertEquals(Integer.valueOf(0), c.getDureeRecup());
        assertNull(c.getDepartHeure());
        assertNull(c.getArriveeHeure());
    }

    @Test
    public void LeNumeroDePassage_fonctionneCorrectement() {
        Collecte c = new Collecte(5L, null, null, 0, 0);
        c.setNumPassage(5); 

        // Vérification de la valeur
        assertEquals(Integer.valueOf(5),c.getNumPassage());
    }
}
