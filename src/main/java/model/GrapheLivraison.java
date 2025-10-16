package model;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import tsp.Graphe;

public class GrapheLivraison implements Graphe {

	int nbSommets;
	int[][] cout;

	/**
	 * Cree un graphe complet dont les aretes ont un cout compris entre COUT_MIN et COUT_MAX
	 * @param nbSommets
	 */
	public GrapheLivraison(
		int nbSommets, 
		Map<Integer, List<SimpleEntry<Integer, Float>>> mapDistances
	) {
		this.nbSommets = nbSommets;
		cout = new int[nbSommets][nbSommets];
		for (int i=0; i<nbSommets; i++) {
			for (int j=0; j<nbSommets; j++) {
				cout[i][j] = Integer.MAX_VALUE;
			}
		}

		for (var key : mapDistances.keySet()) {
			List<SimpleEntry<Integer, Float>> pair = mapDistances.get(key) ;
			for (var entry : pair) {
				cout[key][entry.getKey()] = entry.getValue().intValue();
			}
		}
		printCout();
		
	}

	private void printCout() {
		System.out.println("Matrice des couts :");
		for (int i=0; i<nbSommets; i++) {
			for (int j=0; j<nbSommets; j++) {
				System.out.print(cout[i][j] + "\t");
			}
			System.out.println();
		}
	}

	@Override
	public int getNbSommets() {
		return nbSommets;
	}

	@Override
	public int getCout(int i, int j) {
		if (i<0 || i>=nbSommets || j<0 || j>=nbSommets)
			return -1;
		return cout[i][j];
	}

	@Override
	public boolean estArc(int i, int j) {
		if (i<0 || i>=nbSommets || j<0 || j>=nbSommets)
			return false;
		return i != j;
	}

}
