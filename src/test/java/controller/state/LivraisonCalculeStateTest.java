package controller.state;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import controller.CarteController;

public class LivraisonCalculeStateTest {
	
    @Test
    public void testChargerCarte() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        boolean result = state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        assertTrue(result);
        result = state.chargerCarte(controller, carteController, "");
        assertFalse(result);
    }

    @Test
    public void testChargerLivraison() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        boolean result = state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        assertTrue(result);
        result = state.chargerLivraison(controller, carteController, "");
        assertFalse(result);
    }

    @Test
    public void testAssignerLivreur() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.assignerLivreur(controller, carteController, assignations);

        assertEquals(controller.getCurrentState(), controller.livreurAssigneState);
    }

    @Test
    public void testCalculerLivraisonSuccès() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.assignerLivreur(controller, carteController, assignations);
        state.calculerLivraison(controller, carteController);

        assertEquals(controller.getCurrentState(), controller.livraisonCalculeState);
    }

    @Test(expected = Exception.class)
    public void testCalculerLivraisonEchec() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.calculerLivraison(controller, carteController);

        assertEquals(controller.getCurrentState(), controller.carteChargeState);
    }

    @Test
    public void testGenererFeuillesdeRoute() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        CarteController carteController = new CarteController();
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));

        state.chargerCarte(controller, carteController, "ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        state.chargerLivraison(controller, carteController, "ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        state.assignerLivreur(controller, carteController, assignations);
        state.calculerLivraison(controller, carteController);
        state.genererFeuillesdeRoute(controller, carteController);

        assertEquals(controller.getCurrentState(), controller.livraisonCalculeState);
    }

    @Test
    public void testChangerLivraison() {
        LivraisonCalculeState state = new LivraisonCalculeState();
        Controller controller = new Controller();
        state.changerLivraison(controller);
        assertEquals(controller.getCurrentState(), controller.livraisonChargeState);
    }

}
