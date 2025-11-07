package tsp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import model.GrapheLivraison;
import model.GrapheTotal;
import model.Noeud;
import model.Troncon;

public class IteratorSeqTest {

    @Test
    public void testIteratorSeq() {
        HashMap<Long, Noeud> noeuds = new HashMap<>();
        List<Troncon> troncons = new ArrayList<>();
        noeuds.put(0L, new Noeud(0, 0.0f, 0.0f));
        noeuds.put(1L, new Noeud(1, 0.0f, 0.0f));
        noeuds.put(2L, new Noeud(2, 0.0f, 0.0f));
        troncons.add(new Troncon("EL", 8, noeuds.get(0L), noeuds.get(1L))); 
        GrapheTotal gt = new GrapheTotal(troncons, noeuds, 0);
        
        List<model.Site> sites = new ArrayList<>();
        sites.add(new model.Entrepot(0L, 0.0f, 0.0f));
        sites.add(new model.Collecte(1L, 0.0f, 0.0f, 1, 5));
        sites.add(new model.Depot(2L, 0.0f, 0.0f, 1, 5));
        gt.RechercheDijkstra(sites);
        
        GrapheLivraison g = new GrapheLivraison(gt.getNbSommets(), gt.getMapDistances());
        g.setContrainteHashMap(gt.getContrainteHashMap());
        
        Collection<Integer> nonVus = new java.util.ArrayList<>();
        nonVus.add(0);
        nonVus.add(1);
        nonVus.add(2);
        Iterator<Integer> iterator = new IteratorSeq(nonVus, 0, g);

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
        assertFalse("Le dépôt ne doit pas être proposé avant sa collecte", iterator.hasNext());
    }
    
}
