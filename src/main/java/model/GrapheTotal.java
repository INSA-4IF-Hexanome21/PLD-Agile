package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import tsp.Graphe;

public class GrapheTotal implements Graphe {

	int nbSommets;
	Map<Integer, List<SimpleEntry<Integer, Float>>> mapAllSommets;
	HashMap<Integer, Long> indexToId;
	HashMap<Long, Integer> idToIndex;

	/**
	 * Cree un graphe complet dont les aretes ont un cout compris entre COUT_MIN et COUT_MAX
	 * @param nbSommets
	 */
	public GrapheTotal(
		int nbSommets, 
		List<Troncon> troncons,
		List<Noeud> noeuds,
		long idEntrepot
	) {
		this.nbSommets = nbSommets;
		this.idToIndex = new HashMap<>();
		this.indexToId = new HashMap<>();
		this.mapAllSommets = new HashMap<>();
		Integer compteur = 1;


		for (Noeud noeud : noeuds) {
			long idNoeud = noeud.getId();
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
		return nbSommets;
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

    public Long getIdFromIndex(Integer index) {
        return indexToId.get(index);
    }

	public Map<Integer, List<SimpleEntry<Integer, Float>>> getMapAllSommets(){
		return this.mapAllSommets;
	}
	
}
