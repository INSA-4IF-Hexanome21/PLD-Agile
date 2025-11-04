package controller;

import static org.junit.Assert.*;

import model.Noeud;
import model.DemandeLivraison;
import model.Troncon;

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

        // Vérification du nombre attendu de noeuds (exemple : 5)
        assertEquals("Nombre de noeuds incorrect", 5, mapNoeuds.size());

        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        assertNotNull(troncons);
        assertFalse("Le plan doit contenir des tronçons", troncons.isEmpty());

        // Vérification du nombre attendu de tronçons (exemple : 6)
        assertEquals("Nombre de tronçons incorrect", 6, troncons.size());
    }
    
    @Test
    public void chargerDemandes_formatsHeureEtAccessibilite() {
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";
        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);

        // Cas 1: format heure H:m:s
        String cheminLivraison1 = "ressources/fichiersXMLCollecteDepot/demandePetit1.xml";
        DemandeLivraison demande1 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison1, mapNoeuds);
        assertNotNull(demande1);
        assertNotNull(demande1.getSites());
        //assertNotNull(demande1.getSitesNonAccessibles());
        // Doit au minimum inclure l'entrepôt dans sites ou non accessibles
        //assertTrue(demande1.getSites().size() + demande1.getSitesNonAccessibles().size() >= 1);

        // Cas 2: format heure H:m et adresses potentiellement manquantes
        String cheminLivraison2 = "ressources/fichiersXMLCollecteDepot/myDeliverRequest.xml";
        DemandeLivraison demande2 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison2, mapNoeuds);
        assertNotNull(demande2);
        // Il devrait y avoir au moins une adresse non accessible dans ce fichier de test
       // assertTrue(demande2.getSitesNonAccessibles().size() >= 0);
    }
}
