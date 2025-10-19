package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TronconTest {

    @Test
    public void equalsAndHashCode_useNomEtExtremites() {
        Noeud n1 = new Noeud(1L, 1f, 1f);
        Noeud n2 = new Noeud(2L, 2f, 2f);
        Troncon a = new Troncon("Rue A", 10f, n1, n2);
        Troncon b = new Troncon("Rue A", 20f, n1, n2);
        Troncon c = new Troncon("Rue B", 10f, n1, n2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    public void toString_containsChamps() {
        Troncon t = new Troncon("Rue X", 12.5f, new Noeud(1, 0f, 0f), new Noeud(2, 0f, 0f));
        String s = t.toString();
        assertTrue(s.contains("Rue X"));
        assertTrue(s.contains("origine=1"));
        assertTrue(s.contains("destination=2"));
    }

}
