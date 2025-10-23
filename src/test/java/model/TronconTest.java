package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TronconTest {

    @Test
    public void equalsEtHashCode_Fonctionnent() {
        Noeud n1 = new Noeud(1L, 1f, 1f);
        Noeud n2 = new Noeud(2L, 2f, 2f);
        Troncon a = new Troncon("Rue A", 10f, n1, n2);
        Troncon b = new Troncon("Rue A", 20f, n1, n2);
        Troncon c = new Troncon("Rue B", 10f, n1, n2);
        Integer d = 5;
        assertEquals(a, b);
        assertEquals(a, a);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
    }

    @Test
    public void toStringsEtGetters_ContriennentChanmps() {
        Troncon t1 = new Troncon("Rue X", 12.5f, new Noeud(1, 0f, 0f), new Noeud(2, 0f, 0f));
        String s1 = t1.toString();
        Troncon t2 = new Troncon("Rue Y", 12.5f, null, null);
        String s2 = t2.toString();


        assertTrue(s1.contains("Rue X"));
        assertTrue(s1.contains("origine=1"));
        assertTrue(s1.contains("destination=2"));

        assertTrue(s2.contains("Rue Y"));
        assertTrue(s2.contains("origine=null"));
        assertTrue(s2.contains("destination=null"));

        assertEquals(12.5f, t1.getLongueur(), 0.001f);
        assertEquals("Rue Y", t2.getNomRue());
    }

}
