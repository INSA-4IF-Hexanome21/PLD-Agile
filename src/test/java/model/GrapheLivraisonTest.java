package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import tsp.*;
public class GrapheLivraisonTest {
    
    public static void main(String[] args) {
        System.out.println("====== Test GrapheLivraison ====== ");
        // Exemple de mapDistances:
		// {0=[(1, 2.0), (2, 3.5)], 1=[(0, 2.0), (2, 1.5)], 2=[(0, 3.5), (1, 1.5)]}
        Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances = new HashMap();
		mapDistances.put(0, new ArrayList<SimpleEntry<Integer, Float>>(
			Arrays.asList(
				new SimpleEntry<>(1, 2.0f), 
				new SimpleEntry<>(2, 3.5f)
			)
		)); 
		mapDistances.put(1, new ArrayList<SimpleEntry<Integer, Float>>(
			Arrays.asList(
				new SimpleEntry<>(0, 2.0f), 
				new SimpleEntry<>(2, 1.5f)
			)
		));
		mapDistances.put(2, new ArrayList<SimpleEntry<Integer, Float>>(
			Arrays.asList(
				new SimpleEntry<>(0, 3.5f), 
				new SimpleEntry<>(1, 1.5f)
			)
		));
		for (var key : mapDistances.keySet()) {
			System.out.println("Key: " + key + " -> Value: " + mapDistances.get(key));
		}
        GrapheLivraison graphe = new GrapheLivraison(3, new HashMap<Integer, Long>(), mapDistances);
        TSP tsp = new TSP1();
        long tempsDebut = System.currentTimeMillis();
        tsp.chercheSolution(60000, graphe);
        System.out.print("Solution de longueur "+tsp.getCoutSolution()+" trouvee en "
					+(System.currentTimeMillis() - tempsDebut)+"ms : ");
        for (int i=0; i<graphe.getNbSommets(); i++)
				System.out.print(tsp.getSolution(i)+" ");
    }
}
