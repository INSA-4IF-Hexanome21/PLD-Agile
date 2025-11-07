package model;

import static org.junit.Assert.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import org.junit.Test;

public class DijkstraTest {

    @Test
    public void testDijkstra() {
        Dijkstra dijkstra = new Dijkstra();
        assertNotNull(dijkstra);
    }

    @Test
    public void testDijkstraCheminSimple() {

        // Exemple : https://www.maths-cours.fr/methode/algorithme-de-dijkstra-etape-par-etape
        HashMap<Long, Noeud> noeuds = new HashMap<>();
        noeuds.put(0L, new Noeud(0, 0.0f, 0.0f)); // E
        noeuds.put(1L, new Noeud(1, 0.0f, 0.0f)); // L
        noeuds.put(2L, new Noeud(2, 0.0f, 0.0f)); // M
        noeuds.put(3L, new Noeud(3, 0.0f, 0.0f)); // N
        noeuds.put(4L, new Noeud(4, 0.0f, 0.0f)); // S
        noeuds.put(5L, new Noeud(5, 0.0f, 0.0f)); // T

        List<Troncon> troncons = new ArrayList<>();
        troncons.add(new Troncon("EL", 8, noeuds.get(0L), noeuds.get(1L)));   // E-L
        troncons.add(new Troncon("EM", 10, noeuds.get(0L), noeuds.get(2L)));  // E-M
        troncons.add(new Troncon("ES", 10, noeuds.get(0L), noeuds.get(4L)));  // E-S
        troncons.add(new Troncon("ET", 4, noeuds.get(0L), noeuds.get(5L)));   // E-T
        troncons.add(new Troncon("LM", 7, noeuds.get(1L), noeuds.get(2L)));   // L-M
        troncons.add(new Troncon("LN", 2, noeuds.get(1L), noeuds.get(3L)));   // L-N
        troncons.add(new Troncon("LS", 5, noeuds.get(1L), noeuds.get(4L)));   // L-S
        troncons.add(new Troncon("MN", 4, noeuds.get(2L), noeuds.get(3L)));   // M-N
        troncons.add(new Troncon("NS", 8, noeuds.get(3L), noeuds.get(4L)));   // N-S
        troncons.add(new Troncon("ST", 8, noeuds.get(4L), noeuds.get(5L)));   // S-T

        GrapheTotal gt = new GrapheTotal(troncons, noeuds, 0);
        Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin = new HashMap<>();
        Map<Integer, Float> distancesObtenues = Dijkstra.dijkstra(gt, 2L, cheminsMin);
        Map<Integer, Float> distancesAttendues = new HashMap<>();
        distancesAttendues.put(0, 10f);  // E
        distancesAttendues.put(1, 6f);   // L
        distancesAttendues.put(2, 0f);   // M
        distancesAttendues.put(3, 4f);   // N
        distancesAttendues.put(4, 11f);  // S
        distancesAttendues.put(5, 14f);  // T

        assertEquals(distancesAttendues, distancesObtenues);

    }

    @Test
    public void testDijkstraNoeudIsole() {

        // Exemple : https://www.maths-cours.fr/methode/algorithme-de-dijkstra-etape-par-etape
        HashMap<Long, Noeud> noeuds = new HashMap<>();
        noeuds.put(0L, new Noeud(0, 0.0f, 0.0f)); // E
        noeuds.put(1L, new Noeud(1, 0.0f, 0.0f)); // L
        noeuds.put(2L, new Noeud(2, 0.0f, 0.0f)); // M
        noeuds.put(3L, new Noeud(3, 0.0f, 0.0f)); // N
        noeuds.put(4L, new Noeud(4, 0.0f, 0.0f)); // S
        noeuds.put(5L, new Noeud(5, 0.0f, 0.0f)); // T

        List<Troncon> troncons = new ArrayList<>();
        troncons.add(new Troncon("EL", 8, noeuds.get(0L), noeuds.get(1L)));   // E-L
        troncons.add(new Troncon("EM", 10, noeuds.get(0L), noeuds.get(2L)));  // E-M
        troncons.add(new Troncon("ES", 10, noeuds.get(0L), noeuds.get(4L)));  // E-S
        troncons.add(new Troncon("LM", 7, noeuds.get(1L), noeuds.get(2L)));   // L-M
        troncons.add(new Troncon("LN", 2, noeuds.get(1L), noeuds.get(3L)));   // L-N
        troncons.add(new Troncon("LS", 5, noeuds.get(1L), noeuds.get(4L)));   // L-S
        troncons.add(new Troncon("MN", 4, noeuds.get(2L), noeuds.get(3L)));   // M-N
        troncons.add(new Troncon("NS", 8, noeuds.get(3L), noeuds.get(4L)));   // N-S

        GrapheTotal gt = new GrapheTotal(troncons, noeuds, 0);
        Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin = new HashMap<>();
        Map<Integer, Float> distancesObtenues = Dijkstra.dijkstra(gt, 2L, cheminsMin);
        Map<Integer, Float> distancesAttendues = new HashMap<>();
        distancesAttendues.put(0, 10f);       // E
        distancesAttendues.put(1, 6f);        // L
        distancesAttendues.put(2, 0f);        // M
        distancesAttendues.put(3, 4f);        // N
        distancesAttendues.put(4, 11f);       // S
        distancesAttendues.put(5, Float.MAX_VALUE); // T

        assertEquals(distancesAttendues, distancesObtenues);

    }

}