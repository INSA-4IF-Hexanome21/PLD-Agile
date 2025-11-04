package controller.state;

import static org.junit.Assert.*;

import org.junit.Test;

import controller.CarteController;

public class CarteChargeStateTest {

    @Test
    public void testChargerCarte() {
        CarteChargeState state = new CarteChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        boolean result = state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        assertTrue(result);
        result = state.chargerCarte(controller, carteController, "");
        assertFalse(result);
    }

    @Test
    public void testChargerLivraison() {
        CarteChargeState state = new CarteChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        boolean result = state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        assertTrue(result);
        result = state.chargerLivraison(controller, carteController, "");
        assertFalse(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testCalculerLivraison() {
        CarteChargeState state = new CarteChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.calculerLivraison(controller, carteController);
    }

    @Test(expected = IllegalStateException.class)
    public void testChangerLivraison() {
        CarteChargeState state = new CarteChargeState();
        Controller controller = new Controller();
        state.changerLivraison(controller);
    }
    
}
