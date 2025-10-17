package model;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;


public class Dijsktra {

    public static Map<Integer, Float> dijkstra(
        GrapheTotal gt, 
        Long idNoeudDepart, 
        Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin
    ) {
        // int indexNoeudDepart = gt.idToIndex.get(idNoeudDepart);
        int indexNoeudDepart = gt.getIndexFromId(idNoeudDepart);
        int nbSommets = gt.getNbSommets();
        System.out.println("Nombre de sommets dans le graphe : " + nbSommets);
        
        int[] predecesseurs = new int[nbSommets+1];
        float[] distances = new float[nbSommets+1];
        List<Integer> sommetsVisites = new ArrayList<>();
        boolean[] visites = new boolean[nbSommets+1];
        PriorityQueue<SimpleEntry<Integer, Float>> p = new PriorityQueue<>(
            Comparator.comparing(SimpleEntry::getValue)
        );

        // Initialisation
        for (int i = 0; i < nbSommets + 1; i++) {
            distances[i] = Float.MAX_VALUE;
            predecesseurs[i] = -1;
        }
        distances[indexNoeudDepart] = 0.0f;
        p.add(new SimpleEntry<>(indexNoeudDepart, 0.0f));

        // Tant qu'il reste des sommets non visités
        int s = indexNoeudDepart;
        while (!p.isEmpty()) {
            s = p.poll().getKey();
            //System.out.println("Sommet le plus proche : " + s + " avec distance " + distances[s]);

            List<SimpleEntry<Integer,Float>> voisins = gt.getMapAllSommets().get(s);
            for (SimpleEntry<Integer, Float> voisin : voisins) {
                //System.out.println("Voisin : " + voisin.getKey() + " avec cout " + voisin.getValue());
                int indexVoisin = voisin.getKey();
                float cout = voisin.getValue();
                if (!visites[indexVoisin]) {
                    calculeDistanceMin(gt, s, indexVoisin, predecesseurs, distances, cout, p);   
                }
            }
            sommetsVisites.add(s);
            visites[s] = true;
        }
        
        // Construire les chemins minimaux
        for (int dest= 0; dest<nbSommets; dest++) {
            if (dest != indexNoeudDepart && distances[dest] != Float.MAX_VALUE) {
                List<Integer> chemin = new ArrayList<>();
                for (int v = dest; v != -1; v = predecesseurs[v]) {
                    chemin.add(0, v); // Ajouter au début pour inverser l'ordre
                }
                cheminsMin.put(new SimpleEntry<>(indexNoeudDepart, dest), chemin);
            }
        }
        //System.out.println("Chemins minimaux depuis le noeud " + indexNoeudDepart + " : " + cheminsMin);

        // Conversion des distances en map
        Map<Integer, Float> resultat = new HashMap<>();
        for (int i = 0; i < nbSommets; i++) {
            //System.out.println("Distance du noeud " + indexNoeudDepart + " au noeud " + i + " : " + distances[i]);
            resultat.put(i, distances[i]);
        }
        return resultat;
    }

    
    private static int getSommetLePlusProche(float[] distances, boolean[] visites) {
        float minDistance = Float.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < distances.length; i++) {
            if (!visites[i] && distances[i] < minDistance) {
                minDistance = distances[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private static void calculeDistanceMin(
        GrapheTotal gt, 
        int s, 
        int indexVoisin, 
        int[] predecesseurs, 
        float[] distances, 
        float cout,
        PriorityQueue<SimpleEntry<Integer, Float>> p
    ) {
        if (distances[s] + cout < distances[indexVoisin]) {
            distances[indexVoisin] = distances[s] + cout;
            predecesseurs[indexVoisin] = s;
            p.add(new SimpleEntry<>(indexVoisin, distances[indexVoisin]));
        }
    }
}
