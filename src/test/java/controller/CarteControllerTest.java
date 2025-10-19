package controller;

import static org.junit.Assert.*;

import model.Carte;
import org.junit.Test;

public class CarteControllerTest {

    @Test
    public void chargerEtJson_nonVide() {
        CarteController controller = new CarteController();
        controller.chargerCarteDepuisXML("ressources/fichiersXMLCollecteDepot/petitPlan.xml");
        controller.chargerDemandesDepuisXML("ressources/fichiersXMLCollecteDepot/demandePetit1.xml");
        String json = controller.getCarteJSON();
        assertNotNull(json);
        assertTrue(json.contains("noeuds"));
        assertTrue(json.contains("troncons"));
        assertTrue(json.contains("sites"));
        Carte carte = controller.getCarte();
        assertTrue(carte.getNoeuds().size() > 0);
        assertTrue(carte.getTroncons().size() > 0);
    }

}
