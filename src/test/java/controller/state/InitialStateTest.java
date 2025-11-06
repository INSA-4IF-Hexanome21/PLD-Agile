package controller.state;

import static org.junit.Assert.*;

import org.junit.Test;

import controller.CarteController;

public class InitialStateTest {

    @Test
    public void testChargerCarte() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        boolean result = state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        assertTrue(result);
        result = state.chargerCarte(controller, carteController, "");
        assertFalse(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testChargerLivraison() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
    }

    @Test(expected = IllegalStateException.class)
    public void testCalculerLivraison() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.calculerLivraison(controller, carteController);
    }

    @Test(expected = IllegalStateException.class)
    public void testChangerLivraison() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        state.changerLivraison(controller);
    }

    @Test(expected = IllegalStateException.class)
    public void testAssignerLivreur() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.assignerLivreur(controller, carteController, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testGenererFeuillesdeRoute() {
        InitialState state = new InitialState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.genererFeuillesdeRoute(controller, carteController);
    }

}
