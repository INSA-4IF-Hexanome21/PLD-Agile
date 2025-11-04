package controller;

import static org.junit.Assert.*;

import controller.state.Controller;
import org.junit.Before;
import org.junit.Test;

public class StateTest {

    private Controller controller;

    @Before
    public void setUp() {
        controller = new Controller(); // constructeur par défaut
    }

    @Test
    public void testInitialState() {
        try {
            controller.calculerLivraison();
            fail("Doit lancer IllegalStateException avant de charger une carte");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("charger une carte"));
        }

        try {
            controller.chargerLivraison("dummy.xml");
            fail("Doit lancer IllegalStateException avant de charger une carte");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("charger une carte"));
        }
    }

    @Test
    public void testCarteChargeState() {
        boolean loaded = controller.chargerCarte("ressources/fichiersXMLCollecteDepot/moyenPlan.xml");
        assertTrue("La carte doit être chargée", loaded);

        String json = controller.getCarteJSON();
        assertNotNull(json);
        assertTrue(json.contains("noeuds"));
        assertTrue(json.contains("troncons"));
        assertTrue(json.contains("sites"));
    }

    @Test
    public void testLivraisonChargeState() {
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/moyenPlan.xml");
        boolean loadedLivraison = controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandeMoyen5.xml");
        assertTrue("La livraison doit être chargée", loadedLivraison);
    }

    @Test
    public void testLivraisonCalculeState() {
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/moyenPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandeMoyen5.xml");
        controller.calculerLivraison();
    }

    @Test
    public void testChangerLivraison() {
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/moyenPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandeMoyen5.xml");
        controller.calculerLivraison();
        controller.changerLivraison();
    }
}
