package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class LivreurTest {

    @Test
    public void constructeurEtGetters_fonctionnentCorrectement() {
        // Création d’un livreur
        Livreur l = new Livreur(1L, "Dupont", "Jean");

        // Vérification des valeurs initialisées par le constructeur
        assertEquals(1L, l.getId());
        assertEquals("Dupont", l.getNom());
        assertEquals("Jean", l.getPrenom());
    }

    @Test
    public void valeursSontCorrectementStockees() {
        // Autre cas pour vérifier la cohérence des données
        Livreur l = new Livreur(2L, "Martin", "Claire");

        assertNotNull(l.getNom());
        assertNotNull(l.getPrenom());
        assertTrue(l.getNom().length() > 0);
        assertTrue(l.getPrenom().length() > 0);

        // Vérifie que les données sont bien distinctes d’un autre objet
        Livreur autre = new Livreur(3L, "Durand", "Luc");
        assertNotEquals(l.getId(), autre.getId());
        assertNotEquals(l.getNom(), autre.getNom());
        assertNotEquals(l.getPrenom(), autre.getPrenom());
    }

    @Test
    public void toString_estInformative_siRedefinie() {
        // Si plus tard toString() est redéfini dans Livreur
        Livreur l = new Livreur(4L, "Test", "User");
        String s = l.toString();

        // Cette vérification reste neutre si toString n’est pas encore redéfini
        assertNotNull(s);
    }
}
