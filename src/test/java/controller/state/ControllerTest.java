package controller.state;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerTest {

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
    
}
