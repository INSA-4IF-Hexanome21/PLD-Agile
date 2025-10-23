package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class NoeudTest {

    @Test
    public void gettersEtToString_Fonctionnent() {
        Noeud n = new Noeud(1L, 45.0f, 4.0f);
        assertEquals(1L, n.getId());
        assertEquals(45.0f, n.getLatitude(), 0.0f);
        assertEquals(4.0f, n.getLongitude(), 0.0f);
        String s = n.toString();
        assertTrue(s.contains("id=1"));
        assertTrue(s.contains("latitude=45.0"));
    }

    @Test
    public void equalsAndHashCode_parIDSeulement() {
        Noeud a = new Noeud(2L, 10f, 20f);
        Noeud b = new Noeud(2L, 30f, 40f);
        Noeud c = new Noeud(3L, 10f, 20f);
        int d = 0;
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
    }
    
}
