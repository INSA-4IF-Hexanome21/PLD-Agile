package controller;

import static org.junit.Assert.*;

import model.Noeud;
import model.DemandeLivraison;
import model.Troncon;
import model.Site;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import java.time.LocalTime;

public class GestionnaireXMLTest {

    @Test
    public void TestDeCreationDeGestionnaire(){
        GestionnaireXML gestionnaire = new GestionnaireXML();
        assertNotNull(gestionnaire);
    }

    @Test
    public void chargerPlan_noeudsEtTroncons_petitsFichiers() {
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";

        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);
        assertNotNull(mapNoeuds);
        assertFalse("La carte doit contenir des noeuds", mapNoeuds.isEmpty());
        assertEquals("Nombre de noeuds incorrect", 308, mapNoeuds.size());

        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        assertNotNull(troncons);
        assertFalse("Le plan doit contenir des tronçons", troncons.isEmpty());
        assertEquals("Nombre de tronçons incorrect", 616, troncons.size());
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

        //Cas 2: format heure H:m
        String cheminLivraison2 = "src/test/resources/uploads/plans/demandePetit1Test.xml";
        DemandeLivraison demande2 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison2, mapNoeuds);
        assertNotNull(demande2);
        assertNotNull(demande2.getSites());

        // Cas 3: adresses manquantes
        String cheminLivraison3 = "ressources/fichiersXMLCollecteDepot/myDeliverRequest.xml";
        DemandeLivraison demande3 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison3, mapNoeuds);
        assertNull(demande3);

        //Cas 4: heure invalide
        String cheminLivraison4 = "src/test/resources/uploads/plans/demandePetitTestErr.xml";
        DemandeLivraison demande4 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison4, mapNoeuds);
        Site e = null;
        for (Site site : demande4.getSites()) {
            if (site.getTypeSite()== "entrepot") {
                e = site;
                break;
            }
        }
        assertNotNull("L'entrepôt doit exister dans la demande", e);
        assertEquals("L'heure de départ est correctement mise à jour", LocalTime.of(8, 0), e.getDepartHeure());

        //Cas 5: Collecte Inexistant
        String cheminLivraison5 = "src/test/resources/uploads/plans/demandeCollecteInexistantTest.xml";
        DemandeLivraison demande5 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison5, mapNoeuds);
        assertNull(demande5);

        //Cas 6: Depot Inexistant
        String cheminLivraison6 = "src/test/resources/uploads/plans/demandeDepotInexistantTest.xml";
        DemandeLivraison demande6 = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison6, mapNoeuds);
        assertNull(demande6);
    }

    @Test
    public void TestPlanInnexistant() {
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/fichierInexistant.xml";

        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);
        assertNotNull(mapNoeuds);
        assertTrue("La carte doit être vide si le fichier n'existe pas", mapNoeuds.isEmpty());

        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        assertNotNull(troncons);
        assertTrue("La liste de tronçons doit être vide si le fichier n'existe pas", troncons.isEmpty());
    }

}
