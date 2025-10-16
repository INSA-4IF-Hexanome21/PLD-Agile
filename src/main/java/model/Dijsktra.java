package model;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Dijsktra {

    public static Map<Integer, Float> dijkstra(
        GrapheTotal gt, 
        Long idNoeudDepart, 
        Map<SimpleEntry<Integer, Integer>, List<Integer>> cheminsMin
    ) {
        int indexNoeudDepart = gt.idToIndex.get(idNoeudDepart);
        int nbSommets = gt.getNbSommets();
        
        int[] predecesseurs = new int[nbSommets];
        float[] distances = new float[nbSommets];
        List<Integer> sommetsVisites = new ArrayList<>();

        // Initialisation
        for (int i = 0; i < nbSommets; i++) {
            distances[i] = Float.MAX_VALUE;
            predecesseurs[i] = -1;
        }
        distances[indexNoeudDepart] = 0.0f;

        // Tant qu'il reste des sommets non visités
        int s = indexNoeudDepart;
        while (sommetsVisites.size() < nbSommets) {
            s = getSommetLePlusProche(distances, sommetsVisites);
            if (s == -1) break; // Tous les sommets accessibles ont été visités

            List<SimpleEntry<Integer,Float>> voisins = gt.mapAllSommets.get(s);
            for (SimpleEntry<Integer, Float> voisin : voisins) {
                float cout = gt.getCout(s, voisin.getKey());
                if (!sommetsVisites.contains(voisin.getKey())) {
                    calculeDistanceMin(gt, s, voisin, predecesseurs, distances, cout);   
                }
            }
            sommetsVisites.add(s);
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

        // Conversion des distances en map
        Map<Integer, Float> resultat = new HashMap<>();
        for (int i = 0; i < nbSommets; i++) {
            resultat.put(i, distances[i]);
        }
        return resultat;
    }

    
    private static int getSommetLePlusProche(float[] distances, List<Integer> sommetsVisites) {
        float minDistance = Float.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < distances.length; i++) {
            if (!sommetsVisites.contains(i) && distances[i] < minDistance) {
                minDistance = distances[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private static void calculeDistanceMin(
        GrapheTotal gt, 
        int s, 
        SimpleEntry<Integer, Float> voisin, 
        int[] predecesseurs, 
        float[] distances, 
        float cout
    ) {
        if (distances[s] + cout < distances[voisin.getKey()]) {
            distances[voisin.getKey()] = distances[s] + cout;
            predecesseurs[voisin.getKey()] = s;
        }
    }
}
