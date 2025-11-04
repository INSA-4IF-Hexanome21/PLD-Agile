package controller.state;

import static org.junit.Assert.*;

import org.junit.Test;

import controller.CarteController;

public class LivraisonChargeStateTest {

    @Test
    public void testChargerCarte() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        boolean result = state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        assertTrue(result);
        result = state.chargerCarte(controller, carteController, "");
        assertFalse(result);
    }

    @Test
    public void testChargerLivraison() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        boolean result = state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        assertTrue(result);
        result = state.chargerLivraison(controller, carteController, "");
        assertFalse(result);
    }

    @Test
    public void testCalculerLivraison() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();

        // Test normal flow
        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        try {
            state.calculerLivraison(controller, carteController);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        assertTrue(carteController.getGrapheTotal() != null);

        // Test exception flow
        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        try {
            state.calculerLivraison(controller, carteController);
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Erreur lors du calcul de la tournée"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testChangerLivraison() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        state.changerLivraison(controller);
    }

}
