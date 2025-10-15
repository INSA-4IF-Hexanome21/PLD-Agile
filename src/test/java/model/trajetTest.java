package model;

import static org.junit.Assert.*;

import java.time.LocalTime;
import org.junit.Test;

public class trajetTest {

    @Test
    public void constructeurAvecLivreur_defautsCorrects() {
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
        Trajet trajet = new Trajet(livreur);
        assertEquals(livreur, trajet.getLivreur());
        assertEquals(LocalTime.of(8, 0), trajet.getHeureDebut());
        assertNull(trajet.getHeureFin());
        assertNotNull(trajet.getSites());
        assertTrue(trajet.getSites().isEmpty());
        assertNotNull(trajet.getTroncons());
        assertTrue(trajet.getTroncons().isEmpty());
        assertNotNull(trajet.getSitesNonAccessibles());
        assertTrue(trajet.getSitesNonAccessibles().isEmpty());
    }

    @Test
    public void constructeurSansLivreur_defautsCorrects() {
        Trajet trajet = new Trajet();
        assertNull(trajet.getLivreur());
        assertEquals(LocalTime.of(8, 0), trajet.getHeureDebut());
        assertNull(trajet.getHeureFin());
    }

}