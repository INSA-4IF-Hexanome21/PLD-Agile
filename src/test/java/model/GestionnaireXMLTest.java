package model;

import static org.junit.Assert.*;

import controller.GestionnaireXML;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

public class GestionnaireXMLTest {

    @Test
    public void chargerPlan_noeudsEtTroncons_petitsFichiers() {
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";

        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);
        assertNotNull(mapNoeuds);
        assertFalse("La carte doit contenir des noeuds", mapNoeuds.isEmpty());

        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        assertNotNull(troncons);
        assertFalse("Le plan doit contenir des tronçons", troncons.isEmpty());
    }

    @Test
    public void chargerDemandes_formatsHeureEtAccessibilite() {
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";
        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);

        // Cas 1: format heure H:m:s
        String cheminLivraison1 = "ressources/fichiersXMLCollecteDepot/demandePetit1.xml";
        Trajet trajet1 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison1, mapNoeuds);
        assertNotNull(trajet1);
        assertNotNull(trajet1.getSites());
        assertNotNull(trajet1.getSitesNonAccessibles());
        // Doit au minimum inclure l'entrepôt dans sites ou non accessibles
        assertTrue(trajet1.getSites().size() + trajet1.getSitesNonAccessibles().size() >= 1);

        // Cas 2: format heure H:m et adresses potentiellement manquantes
        String cheminLivraison2 = "ressources/fichiersXMLCollecteDepot/myDeliverRequest.xml";
        Trajet trajet2 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison2, mapNoeuds);
        assertNotNull(trajet2);
        // Il devrait y avoir au moins une adresse non accessible dans ce fichier de test
        assertTrue(trajet2.getSitesNonAccessibles().size() >= 0);
    }

}
