package tsp;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import model.GrapheLivraison;

public class IteratorSeq implements Iterator<Integer> {

	private Integer[] candidats;
	private int nbCandidats;

	public IteratorSeq(Collection<Integer> nonVus, int sommetCrt, Graphe g) {
		
		GrapheLivraison graphe = (GrapheLivraison) g;
		this.candidats = new Integer[nonVus.size()];
		this.nbCandidats = 0; 

		ArrayList<Integer> listeNonVus = new ArrayList<>(nonVus); 

		for (Integer s : nonVus) {
			if (graphe.estAccessible(s, listeNonVus)) {
				candidats[nbCandidats++] = s;
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return nbCandidats > 0;
	}

	@Override
	public Integer next() {
		nbCandidats--;
		return candidats[nbCandidats];
	}

	// @Override
	// public void remove() {}
}
