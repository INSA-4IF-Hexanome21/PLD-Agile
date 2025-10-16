package model;

import java.util.*;
public class GrapheTotalTest {
    
    public static void main(String[] args) {
        System.out.println("====== Test GrapheTotal ====== ");
        int nbSommets = 2;
		long idEntrepot = 5678;
		Noeud n1 = new Noeud(1234, 5.2f, 45.4f);
		Noeud n2 = new Noeud(5678, 5.4f, 45.8f);
		Noeud n3 = new Noeud(9123, 5.5f, 45.9f);

		Troncon t1 = new Troncon("test", 45, n1, n2);
		Troncon t2 = new Troncon("test2", 32, n2, n3);

		System.out.println("Initialisation Noeuds");
		HashMap<Long,Noeud> noeuds = new HashMap<Long,Noeud>();
		noeuds.put(n1.getId(),n1);
		noeuds.put(n2.getId(),n2);
		noeuds.put(n3.getId(),n3);
		

		System.out.println("Initialisation Troncons");
		List<Troncon> troncons = new ArrayList<Troncon>();
		troncons.add(t1);
		troncons.add(t2);

		System.out.println("Création graphe");
		GrapheTotal gt = new GrapheTotal(nbSommets, troncons, noeuds, idEntrepot);

		System.out.println("Liste d'adjacence");
		System.out.println(gt.getMapAllSommets());

		System.out.println("Conversion via index (5678, 1234)");
		System.out.println(gt.getIdFromIndex(0));
		System.out.println(gt.getIdFromIndex(1));

		System.out.println("Longueur entre sommet 0 et 1 (normalement 45)");
		System.out.println(gt.getCout(0,1));

		System.out.println("Existence ou non des arcs (true,false)");
		System.out.println(gt.estArc(0,1));
		System.out.println(gt.estArc(1,2));

		List<Site> sites = new ArrayList<Site>();
		Entrepot e = new Entrepot(idEntrepot);
		Collecte c = new Collecte(5678,n1.getLatitude(),n1.getLongitude(), 1, 10);
		Depot l = new Depot(5678,n2.getLatitude(),n2.getLongitude(), 1, 10);
		sites.add(e);
		sites.add(l);
		sites.add(c);
		gt.printGraphe();
		gt.RechercheDijkstra( sites);
    }
}
