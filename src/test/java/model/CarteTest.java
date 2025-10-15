package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CarteTest {

    @Test
    public void testAjouterEtSupprimerTrajet() {
        Carte carte = new Carte();
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
        Trajet trajet = new Trajet(livreur);

        // initialement vide
        assertEquals(0, carte.getTrajets().size());

        // après ajout
        carte.ajouterTrajet(trajet);
        assertEquals(1, carte.getTrajets().size());
        assertTrue(carte.getTrajets().contains(trajet));

        // après suppression
        carte.supprimerTrajet(trajet);
        assertEquals(0, carte.getTrajets().size());
        assertFalse(carte.getTrajets().contains(trajet));
    }

}