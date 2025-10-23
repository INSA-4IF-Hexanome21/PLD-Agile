package model;

import static org.junit.Assert.*;

import java.time.LocalTime;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class TrajetTest {

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

    @Test
    public void GettersSettersEtToString_fonctionnent() {
        Trajet trajet1 = new Trajet();
        Trajet trajet2 = new Trajet();
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
        Noeud n1 = new Noeud(1L, 1f, 1f);
        Noeud n2 = new Noeud(2L, 2f, 2f);
        Troncon a = new Troncon("Rue A", 10f, n1, n2);
        Troncon b = new Troncon("Rue B", 20f, n2, n1);
        Troncon c = new Troncon("Rue B", 10f, n1, n2);
        List<Troncon> troncons = Arrays.asList(a, b, c);


        trajet1.setLivreur(livreur);
        trajet1.setdureeTrajet(2.5f);
        trajet1.setTroncons(troncons);
        String s1 = trajet1.toString();

        assertEquals(2.5f, trajet1.getdureeTrajet(), 0.001f);
        assertNull(trajet1.getHeureFin());
       
    }

}
