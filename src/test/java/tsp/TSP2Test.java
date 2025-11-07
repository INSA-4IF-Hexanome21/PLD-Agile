package tsp;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import model.GrapheLivraison;

public class TSP2Test {

    private TSP2 tsp;
    private GrapheLivraison g;

    @Before
    public void setUp() {
        tsp = new TSP2();

        // Création du graphe avec distances
        Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            mapDistances.put(i, new ArrayList<>());
        }

        // Ajout des arcs pour que estAccessible fonctionne
        mapDistances.get(0).add(new SimpleEntry<>(1, 1.0f));
        mapDistances.get(0).add(new SimpleEntry<>(2, 1.0f));
        mapDistances.get(0).add(new SimpleEntry<>(3, 1.0f));
        mapDistances.get(1).add(new SimpleEntry<>(0, 1.0f));
        mapDistances.get(1).add(new SimpleEntry<>(2, 1.0f));
        mapDistances.get(1).add(new SimpleEntry<>(3, 1.0f));
        mapDistances.get(2).add(new SimpleEntry<>(0, 1.0f));
        mapDistances.get(2).add(new SimpleEntry<>(1, 1.0f));
        mapDistances.get(2).add(new SimpleEntry<>(3, 1.0f));
        mapDistances.get(3).add(new SimpleEntry<>(0, 1.0f));
        mapDistances.get(3).add(new SimpleEntry<>(1, 1.0f));
        mapDistances.get(3).add(new SimpleEntry<>(2, 1.0f));

        // Utilisation de la variable d’instance g
        g = new GrapheLivraison(4, mapDistances);

        // Initialisation de contrainteHashMap pour éviter NullPointerException
        // Cette ligne initialise le hashmap vide si nécessaire
        g.setContrainteHashMap(new HashMap<>()); 
    }

    @Test
    public void testChercheSolution() {
        tsp.chercheSolution(2000, (tsp.Graphe) g);
        int cout = tsp.getCoutSolution();
        assertTrue("Le coût doit être >= 0", cout >= 0);
    }

    @Test
    public void testGetSolution_Default() {
        assertEquals(Integer.valueOf(-1), tsp.getSolution(0));
        assertEquals(-1, tsp.getCoutSolution());
    }

    @Test
    public void testGetSolution_AfterSearch() {
        tsp.chercheSolution(1000, (tsp.Graphe) g);
        for (int i = 0; i < g.getNbSommets(); i++) {
            int s = tsp.getSolution(i);
            assertTrue("Sommet doit être valide", s >= 0 && s < g.getNbSommets());
        }
    }

    @Test
    public void testIteratorSeqBehavior() {
        Collection<Integer> nonVus = Arrays.asList(1, 2, 3);
        Iterator<Integer> it = tsp.iterator(0, nonVus, (tsp.Graphe) g);

        List<Integer> visited = new ArrayList<>();
        while (it.hasNext()) {
            visited.add(it.next());
        }

        assertEquals(3, visited.size());
        assertTrue(visited.containsAll(nonVus));
    }
}
