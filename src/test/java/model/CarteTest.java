package model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.time.LocalTime;

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

    @Test
    public void testGetTrajetsRetourneCopie() {
        Carte carte = new Carte();
        Trajet trajet = new Trajet(new Livreur(0, "A", "B"));
        carte.ajouterTrajet(trajet);

        List<Trajet> trajetsRecuperes = new ArrayList<>(carte.getTrajets());
        trajetsRecuperes.remove(trajet);

        assertEquals(1, carte.getTrajets().size());
        assertTrue(carte.getTrajets().contains(trajet));
    }

    @Test
    public void testMajTrajetDepuisChemin() {
        // Création des sites
        Depot depot = new Depot(1L, 0, 10);
        Collecte collecte1 = new Collecte(2L, 5, 5);
        Collecte collecte2 = new Collecte(3L, 3, 2);

        // Création du trajet
        Livreur livreur = new Livreur(1, "Marin", "Bob");
        Trajet trajet = new Trajet(livreur);
        trajet.getSites().add(depot);
        trajet.getSites().add(collecte1);
        trajet.getSites().add(collecte2);

        // Création des noeuds
        Noeud nDepot = new Noeud(depot.getId(), 0f, 0f);
        Noeud nC1 = new Noeud(collecte1.getId(), 1f, 1f);
        Noeud nC2 = new Noeud(collecte2.getId(), 2f, 2f);

        HashMap<Long, Noeud> noeuds = new HashMap<>();
        noeuds.put(nDepot.getId(), nDepot);
        noeuds.put(nC1.getId(), nC1);
        noeuds.put(nC2.getId(), nC2);

        // Création des tronçons (arcs)
        Troncon t1 = new Troncon("Rue A", 10f, nDepot, nC1);
        Troncon t2 = new Troncon("Rue B", 5f, nC1, nC2);
        Troncon t3 = new Troncon("Rue C", 8f, nC2, nDepot);

        List<Troncon> troncons = Arrays.asList(t1, t2, t3);

        // Création du graphe
        GrapheTotal graphe = new GrapheTotal(troncons, noeuds, depot.getId());

        // Chemin complet et solution (indices)
        List<Long> cheminComplet = Arrays.asList(1L, 2L, 3L, 1L); 
        List<Integer> solution = Arrays.asList(0, 1, 2, 0);

        // Carte
        Carte carte = new Carte();

        // Appel de la méthode
        carte.majTrajetDepuisChemin(graphe, cheminComplet, solution, trajet);

        // Assertions
        for (Site s : trajet.getSites()) {
            assertNotNull("L'arrivée du site " + s.getId() + " ne doit pas être null", s.getArriveeHeure());
        }
        assertEquals(depot, trajet.getSites().get(0));
        assertEquals(collecte1, trajet.getSites().get(1));
        assertEquals(collecte2, trajet.getSites().get(2));
        assertNotNull("Heure de fin du trajet non calculée", trajet.getHeureFin());
    }



}
