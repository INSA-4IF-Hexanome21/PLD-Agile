package model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import org.junit.Test;

public class CarteBehaviorTest {

    @Test
    public void ajouterNoeud_neDupliquePas() {
        Carte carte = new Carte();
        Noeud n1 = new Noeud(1L, 0f, 0f);
        Noeud n2 = new Noeud(1L, 1f, 1f);
        carte.ajouterNoeud(n1);
        carte.ajouterNoeud(n2); // même id
        assertEquals(1, carte.getNoeuds().size());
        assertTrue(carte.getNoeuds().containsKey(1L));
    }

    @Test
    public void ajouterTroncon_neDupliquePasSelonEquals() {
        Carte carte = new Carte();
        Noeud a = new Noeud(1L, 0f, 0f);
        Noeud b = new Noeud(2L, 0f, 0f);
        Troncon t1 = new Troncon("A", 10f, a, b);
        Troncon t2 = new Troncon("A", 15f, a, b); // equals à t1
        carte.ajouterTroncon(t1);
        carte.ajouterTroncon(t2);
        assertEquals(1, carte.getTroncons().size());
    }

    @Test
    public void ajouterEtSupprimerSite_fonctionne() {
        Carte carte = new Carte();
        Site s = new Entrepot(123L);
        carte.ajouterSite(s);
        assertEquals(1, carte.getSites().size());
        carte.supprimerSite(s);
        assertEquals(0, carte.getSites().size());
    }

    @Test
    public void settersRemplacentCollections() {
        Carte carte = new Carte();
        HashMap<Long, Noeud> map = new HashMap<>();
        map.put(42L, new Noeud(42L, 1f, 2f));
        carte.setNoeuds(map);
        assertEquals(1, carte.getNoeuds().size());

        List<Troncon> troncons = java.util.Arrays.asList(
                new Troncon("X", 1f, new Noeud(1, 0f, 0f), new Noeud(2, 0f, 0f))
        );
        carte.setTroncons(troncons);
        assertEquals(1, carte.getTroncons().size());
    }
    
}
