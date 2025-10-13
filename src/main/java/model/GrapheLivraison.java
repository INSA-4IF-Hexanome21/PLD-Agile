package model;

import java.util.HashMap;

import tsp.Graphe;

public class GrapheLivraison implements Graphe {

	int nbSommets;
	int[][] cout;
	HashMap<Integer, Long> mapSommets;

	/**
	 * Cree un graphe complet dont les aretes ont un cout compris entre COUT_MIN et COUT_MAX
	 * @param nbSommets
	 */
	public GrapheLivraison(int nbSommets){
		this.nbSommets = nbSommets;
		cout = new int[nbSommets][nbSommets];
		for (int i=0; i<nbSommets; i++){
		    for (int j=0; j<nbSommets; j++){
		        if (i == j) cout[i][j] = -1;
		        else {
		            
		        }
		    }
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

    public HashMap<Integer, Long> getMapSommets() {
        return mapSommets;
    }

    public Long getIdFromIndex(int index) {
        return mapSommets.get(index);
    }
}
