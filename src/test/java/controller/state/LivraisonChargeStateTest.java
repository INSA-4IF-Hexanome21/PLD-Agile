package controller.state;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

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

    @Test(expected = IllegalStateException.class)
    public void testCalculerLivraison() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.assignerLivreur(controller, carteController, assignations);
        
        state.calculerLivraison(controller, carteController);
    }

    @Test(expected = IllegalStateException.class)
    public void testChangerLivraison() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        state.changerLivraison(controller);
    }

    @Test
    public void testAssignerLivreur() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.assignerLivreur(controller, carteController, assignations);

        assertEquals(controller.getCurrentState(), controller.livreurAssigneState);
    }

    @Test(expected = IllegalStateException.class)
    public void testGenererFeuillesdeRoute() {
        LivraisonChargeState state = new LivraisonChargeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.genererFeuillesdeRoute(controller, carteController);
    }

}
