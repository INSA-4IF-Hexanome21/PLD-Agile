package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import tsp.Graphe;

public class GrapheTotal implements Graphe {

	int nbSommets;
	Map<Integer, List<SimpleEntry<Integer, Float>>> mapAllSommets;
	HashMap<Integer, Long> indexToId;
	HashMap<Long, Integer> idToIndex;
	Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances;
	Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin;

	/**
	 * Cree un graphe complet dont les aretes ont un cout compris entre COUT_MIN et COUT_MAX
	 * @param nbSommets
	 */
	public GrapheTotal(
		int nbSommets, 
		List<Troncon> troncons,
		HashMap<Long, Noeud> noeuds,
		long idEntrepot
	) {
		this.nbSommets = nbSommets;
		this.idToIndex = new HashMap<>();
		this.indexToId = new HashMap<>();
		this.mapAllSommets = new HashMap<>();
		Integer compteur = 1;


		for (long idNoeud : noeuds.keySet()) {
			Integer valeurSommet;
			if(idNoeud == idEntrepot){
				valeurSommet = 0;
			}
			else{
				valeurSommet = compteur;
				compteur += 1;
			}
			indexToId.put(valeurSommet,idNoeud);
			idToIndex.put(idNoeud,valeurSommet);
			mapAllSommets.put(valeurSommet,new ArrayList<SimpleEntry<Integer, Float>>());
		}
		
		for (Troncon troncon : troncons){

			//On récupère les ids des noeuds
			Long idNoeud1 = troncon.getOrigine().getId();
			Long idNoeud2 = troncon.getDestination().getId();

			//On convertit les id en entier grâce à la table de conversion
			Integer indexNoeud1 = idToIndex.get(idNoeud1);
			Integer indexNoeud2 = idToIndex.get(idNoeud2);

			System.out.println(indexNoeud1);
			System.out.println(indexNoeud2);


			//Ajout des troncons à la liste d'adjacence
			mapAllSommets.get(indexNoeud1).add(new SimpleEntry<>(indexNoeud2, troncon.getLongueur()));
			mapAllSommets.get(indexNoeud2).add(new SimpleEntry<>(indexNoeud1, troncon.getLongueur()));
		}
	}

	@Override
	public int getNbSommets() {
		return mapAllSommets.size();
	}

	@Override
	//Renvoie le cout d'un couple de sommet si il existe sinon renvoie +inf
	public int getCout(int i, int j) {
		List<SimpleEntry<Integer, Float>> voisins = mapAllSommets.get(i);
		for(SimpleEntry<Integer, Float> voisin : voisins ){
			if (voisin.getKey().equals(j)) {
            	return Math.round(voisin.getValue());
        	}
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean estArc(int i, int j) {
		List<SimpleEntry<Integer, Float>> voisins = mapAllSommets.get(i);
		for(SimpleEntry<Integer, Float> voisin : voisins ){
			if (voisin.getKey().equals(j)) {
            	return true;
        	}
		}
		return false;
	}

	public void printGraphe() {
		System.out.println("===================================");
		System.out.print("Graphe : {");
		boolean firstEntry = true;
		for (var entry : mapAllSommets.entrySet()) {
			if (!firstEntry) System.out.print(", ");
			firstEntry = false;
			System.out.println();
			System.out.print(entry.getKey() + "=[");
			boolean firstVoisin = true;
			for (var voisin : entry.getValue()) {
				if (!firstVoisin) System.out.print(", ");
				firstVoisin = false;
				System.out.printf(Locale.US, "(%d, %.1f)", voisin.getKey(), voisin.getValue());
			}
			System.out.print("]");
		}
		System.out.println("}");
	/* 	System.out.println("mapAllSommets keys: " + mapAllSommets.keySet());
		System.out.println("idToIndex: " + idToIndex);
		System.out.println("indexToId: " + indexToId);
		System.out.println("getNbSommets(): " + getNbSommets()); */
		System.out.println("===================================");
	}

    public Long getIdFromIndex(Integer index) {
        return indexToId.get(index);
    }

	public Map<Integer, List<SimpleEntry<Integer, Float>>> getMapAllSommets(){
		return this.mapAllSommets;
	}

	public Map<Integer, List<SimpleEntry<Integer, Float>>> getMapDistances(){
		return this.mapDistances;
	}

	public Map<SimpleEntry<Integer, Integer>, List<Integer>> getCheminsMin(){
		return this.cheminsMin;
	}
	
	public void RechercheDijkstra(List<Site> sites) {
		this.cheminsMin = new HashMap<>(); 
		this.mapDistances = new HashMap<>();  

		// Créer un Set des indices correspondant aux sites
		Set<Integer> indicesSites = new HashSet<>();
		for (Site site : sites) {
			indicesSites.add(this.idToIndex.get(site.getId()));
		}

		for (Site siteDepart : sites) { 
			int indexDepart = this.idToIndex.get(siteDepart.getId()); 
			// Calculer les distances minimales depuis le site de départ vers tous les autres sommets du gt
			Map<Integer, Float> distancesDepuisDepart = Dijsktra.dijkstra(this, siteDepart.getId(), cheminsMin);  

			List<SimpleEntry<Integer, Float>> voisins = new ArrayList<>(); 
			
			// Pour chaque autre site (pas tous les nœuds, juste les sites)
			for (Site siteArrivee: sites) {
				int indexArrivee = this.idToIndex.get(siteArrivee.getId());
				
				if (indexDepart != indexArrivee) {
					Float cout = distancesDepuisDepart.get(indexArrivee);
					
					if (cout != null && cout != Float.MAX_VALUE) {
						// Enregistre la distance minimale entre les deux sites
						voisins.add(new SimpleEntry<>(indexArrivee, cout));
					}
				}
			}
			
			mapDistances.put(indexDepart, voisins); 
		}  

		System.out.println("\n=== Chemins et distances minimales entre sites ==="); 
		for (var entry : mapDistances.entrySet()) { 
			int depart = entry.getKey();
			
			for (SimpleEntry<Integer, Float> voisin : entry.getValue()) {
				int arrivee = voisin.getKey();
				float distance = voisin.getValue();
				System.out.printf("De %d vers %d (distance: %.1f)", depart, arrivee, distance);
				List<Integer> chemin = cheminsMin.get(new SimpleEntry<>(depart, arrivee));
				System.out.println(chemin != null && !chemin.isEmpty() ? " via " : " (direct)");
				if (chemin != null && !chemin.isEmpty()) {
					// Afficher le chemin avec les nœuds intermédiaires
					for (int i = 0; i < chemin.size(); i++) {
						System.out.print(chemin.get(i));
						if (i < chemin.size() - 1) {
							System.out.print(" -> ");
						}
					}
				}
				System.out.println();
			}
		}
	}

	public List<Integer> getCheminComplet(List<Integer> solution) {
		List<Integer> cheminComplet = new ArrayList<>();
		for(int i = 0; i < solution.size() - 1; i++) {
			int indexDepart = solution.get(i);
			int indexArrivee = solution.get(i + 1);
			cheminComplet.addAll(cheminsMin.get(new SimpleEntry<>(indexDepart, indexArrivee))); 
		}
		return cheminComplet;
	}

	public List<Long> convertirCheminComplet(List<Integer> chemin){
		List<Long> cheminCompletConverti = new ArrayList<>();
		for(Integer index : chemin) {
			cheminCompletConverti.add(getIdFromIndex(index));
		}
		return cheminCompletConverti;
	}
}
