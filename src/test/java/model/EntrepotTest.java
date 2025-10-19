package model;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

public class EntrepotTest {

    @Test
    public void constructeursEtGetters_fonctionnentCorrectement() {
        // Test du constructeur complet
        Entrepot e1 = new Entrepot(1L, 45.0f, 4.0f);

        // Vérifie les valeurs du constructeur complet
        assertEquals(Long.valueOf(1L), Long.valueOf(e1.getId()));
        assertEquals(Float.valueOf(45.0f), Float.valueOf(e1.getLatitude()));
        assertEquals(Float.valueOf(4.0f), Float.valueOf(e1.getLongitude()));

        // Test du constructeur minimal
        Entrepot e2 = new Entrepot(2L);

        // Vérifie que lat/lng sont null
        assertEquals(Long.valueOf(2L), Long.valueOf(e2.getId()));
        assertNull(e2.getLatitude());
        assertNull(e2.getLongitude());
    }

    @Test
    public void getTypeSite_retourneEntrepot() {
        Entrepot e = new Entrepot(3L);
        assertEquals("entrepot", e.getTypeSite());
    }

    @Test
    public void heures_fonctionnentCorrectement() {
        Entrepot e = new Entrepot(5L, 50.0f, 3.0f);

        LocalTime depart = LocalTime.of(7, 0);
        LocalTime arrivee = LocalTime.of(8, 30);

        e.setDepartHeure(depart);
        e.setArriveeHeure(arrivee);

        assertEquals(depart, e.getDepartHeure());
        assertEquals(arrivee, e.getArriveeHeure());
    }

    @Test
    public void constructeurMinimal_fonctionneCorrectement() {
        Entrepot e = new Entrepot(6L);

        assertEquals(Long.valueOf(6L), Long.valueOf(e.getId()));
        assertNull(e.getLatitude());
        assertNull(e.getLongitude());
        assertNull(e.getDepartHeure());
        assertNull(e.getArriveeHeure());
    }
}
