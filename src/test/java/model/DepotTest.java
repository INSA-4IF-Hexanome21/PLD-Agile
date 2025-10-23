package model;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

public class DepotTest {

    @Test
    public void getters_fonctionnentCorrectement() {
        // Test du constructeur complet
        Depot d = new Depot(1L, 45.0f, 4.0f, 7, 180);

        // Vérifie les getters
        assertEquals(Long.valueOf(1L), Long.valueOf(d.getId()));
        assertEquals(Float.valueOf(45.0f), Float.valueOf(d.getLatitude()));
        assertEquals(Float.valueOf(4.0f), Float.valueOf(d.getLongitude()));
        assertEquals(Integer.valueOf(7), d.getNumLivraison());
        assertEquals(Integer.valueOf(180), d.getDureeRecup());
    }

    @Test
    public void setters_fonctionnentCorrectement() {
        // Test du constructeur minimal
        Depot d = new Depot(2L, 5, 90);

        // Modification via les setters
        d.setNumLivraison(10);
        d.setDureeRecup(200);

        // Vérification des changements
        assertEquals(Integer.valueOf(10), d.getNumLivraison());
        assertEquals(Integer.valueOf(200), d.getDureeRecup());
    }

    @Test
    public void getTypeSite_retourneLivraison() {
        Depot d = new Depot(3L, 8, 100);
        assertEquals("depot", d.getTypeSite());
    }

    @Test
    public void heures_fonctionnentCorrectement() {
        Depot d = new Depot(4L, 12.0f, 24.0f, 3, 120);

        // Test des setters hérités de Site
        LocalTime depart = LocalTime.of(9, 0);
        LocalTime arrivee = LocalTime.of(10, 30);

        d.setDepartHeure(depart);
        d.setArriveeHeure(arrivee);

        // Vérification des getters
        assertEquals(depart, d.getDepartHeure());
        assertEquals(arrivee, d.getArriveeHeure());
    }

    @Test
    public void constructeurMinimal_fonctionneCorrectement() {
        Depot d = new Depot(5L, 0, 0);

        // Vérification des valeurs initiales
        assertEquals(Long.valueOf(5L), Long.valueOf(d.getId()));
        assertNull(d.getLatitude());
        assertNull(d.getLongitude());
        assertEquals(Integer.valueOf(0), d.getNumLivraison());
        assertEquals(Integer.valueOf(0), d.getDureeRecup());
        assertNull(d.getDepartHeure());
        assertNull(d.getArriveeHeure());
    }
}
