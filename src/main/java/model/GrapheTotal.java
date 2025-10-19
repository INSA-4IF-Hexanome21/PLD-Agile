package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import tsp.Graphe;

public class GrapheTotal implements Graphe {

	private int nbSommets;
	private Map<Integer, List<SimpleEntry<Integer, Float>>> mapAllSommets;
	
	private Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances;
	private Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin;

	//Tables de conversion
	private HashMap<Integer, Long> indexToId;
	private HashMap<Long, Integer> idToIndex;
	private HashMap<SimpleEntry<Long, Long>, Troncon> tronconHashMap;
	private HashMap<Long, Noeud> noeudsHashMap;
	private HashMap<Integer,Integer> contrainteHashMap = new HashMap<Integer,Integer>();
	
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
		this.tronconHashMap = new HashMap<>();
		this.noeudsHashMap = noeuds;
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

/* 			System.out.println(indexNoeud1);
			System.out.println(indexNoeud2); */


			//Ajout des troncons à la liste d'adjacence
			mapAllSommets.get(indexNoeud1).add(new SimpleEntry<>(indexNoeud2, troncon.getLongueur()));
			mapAllSommets.get(indexNoeud2).add(new SimpleEntry<>(indexNoeud1, troncon.getLongueur()));

			//Ajout des tronçon dans la table de conversion
			if(idNoeud1 < idNoeud2){
				tronconHashMap.put(new SimpleEntry<>(idNoeud1, idNoeud2), troncon);
			}
			else{
				tronconHashMap.put(new SimpleEntry<>(idNoeud2, idNoeud1), troncon);
			}
			
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

	public Integer getIndexFromId(Long id) {
        return idToIndex.get(id);
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
	
	private void setContrainteHashMap(HashMap<Integer,Integer> contrainteHashMap) {
		this.contrainteHashMap = contrainteHashMap;
		System.out.println("Contrainte HashMap définie : " + this.contrainteHashMap);
	}

	public HashMap<Integer,Integer> getContrainteHashMap(){
		return this.contrainteHashMap;
	}

	public void RechercheDijkstra(List<Site> sites) {
		this.cheminsMin = new HashMap<>(); 
		this.mapDistances = new HashMap<>();  

		// Créer un Set des indices correspondant aux sites
		Set<Integer> indicesSites = new HashSet<>();

		HashMap<Integer,Integer> numLivraisonEtSite = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> depotCollecteHashMap = new HashMap<Integer,Integer>();

		for (Site site : sites) {
			Integer indexSite = this.idToIndex.get(site.getId());
			indicesSites.add(indexSite);
			String type = site.getTypeSite();
			if(type == "depot"){
				Integer numlivraison = ((Depot)site).getNumLivraison();
				Integer indexCollecte = numLivraisonEtSite.get(numlivraison);
				if(indexCollecte == null){
					numLivraisonEtSite.put(numlivraison,indexSite);
				}
				else{
					depotCollecteHashMap.put(indexSite,indexCollecte);
				}
			}

			else if(type == "collecte"){
				Integer numlivraison = ((Collecte)site).getNumLivraison();
				Integer indexDepot = numLivraisonEtSite.get(numlivraison);
				if(indexDepot == null){
					numLivraisonEtSite.put(numlivraison,indexSite);
				}
				else{
					depotCollecteHashMap.put(indexDepot,indexSite);
				}
			}
		}
		this.setContrainteHashMap(depotCollecteHashMap);

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

		//printCheminsEtDistances(sites);
	}

	private void printCheminsEtDistances(List<Site> sites) {
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
		/* System.out.println("=== Reconstruction du chemin complet ===");
		System.out.println("Solution : " + solution); */
		
		List<Integer> cheminComplet = new ArrayList<>();
		
		for(int i = 1 ; i < solution.size(); i++) {
			int indexArrivee = solution.get(i);
			int indexDepart = solution.get(i - 1);
			System.out.println(getIdFromIndex(indexDepart)+" -> "+getIdFromIndex(indexArrivee) + " (" + indexDepart+" -> "+indexArrivee + ");");
			
			SimpleEntry<Integer, Integer> cle = new SimpleEntry<>(indexDepart, indexArrivee);
			List<Integer> chemin = cheminsMin.get(cle);
			
			/* System.out.printf("Étape %d : %d -> %d : %s%n", 
				i, indexDepart, indexArrivee, 
				chemin != null ? chemin.toString() : "Chemin non trouvé"); */
			
			if (chemin == null) return null;
			
			if (i == 0) cheminComplet.addAll(chemin);
			else cheminComplet.addAll(chemin.subList(1, chemin.size()));
			
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

	public Troncon NoeudstoTroncon(long idNoeud1,long idNoeud2){
		Troncon troncon = tronconHashMap.get(new SimpleEntry<>(idNoeud1, idNoeud2));
		if(troncon == null){
			troncon =  tronconHashMap.get(new SimpleEntry<>(idNoeud2, idNoeud1));
		}
		return troncon;
	}

	public Noeud trouverNoeud(long idNoeud){
		return this.noeudsHashMap.get(idNoeud);
	}
}
