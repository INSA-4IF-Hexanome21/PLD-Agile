package controller.state;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class ControllerTest {

    @Test
    public void testChargerCarte() {
        Controller controller = new Controller();
        boolean result = controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        assertTrue(result);
        result = controller.chargerCarte("");
        assertFalse(result);
    }

    @Test
    public void testChargerLivraison() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        boolean result = controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        assertTrue(result);
        result = controller.chargerLivraison("");
        assertFalse(result);
    }

    @Test
    public void testGetCarteJSON() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        String json = controller.getCarteJSON();
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    public void testAssignerLivreur() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));
        controller.assignerLivreur(assignations);
        
        assertEquals(controller.getCurrentState(), controller.livreurAssigneState);
    }

    @Test
    public void testCalculerLivraison() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));
        controller.assignerLivreur(assignations);
        controller.calculerLivraison();
        
        assertEquals(controller.getCurrentState(), controller.livraisonCalculeState);
    }

    @Test
    public void testGenererFeuillesdeRoute() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));
        controller.assignerLivreur(assignations);
        controller.calculerLivraison();
        controller.genererFeuillesdeRoute();
        
        assertEquals(controller.getCurrentState(), controller.livraisonCalculeState);
    }

    @Test
    public void testChangerLivraison() {
        Controller controller = new Controller();
        controller.chargerCarte("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        controller.chargerLivraison("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        
        HashMap<String, List<String>> assignations = new HashMap<>();
        assignations.put("1", List.of("1"));
        controller.assignerLivreur(assignations);
        controller.calculerLivraison();
        controller.changerLivraison();
        
        assertEquals(controller.getCurrentState(), controller.livraisonChargeState);
    }
    
}
