package model;

import java.util.List;
import java.util.Map;

public class GestionnaireXMLTest {

    public static void main(String[] args) {
        System.out.println("===== TEST Gestionnaire XML =====");

        // Chemin vers le fichier plan
        String cheminPlan = "ressources/fichiersXMLPickupDelivery/petitPlan.xml";

        // --- Charger la Map de noeuds ---
        Map<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);
        System.out.println("===== NOEUDS =====");
        for (Noeud n : mapNoeuds.values()) {
            System.out.println(n);
        }

        // --- Charger la liste des tronçons ---
        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        System.out.println("\n===== TRONÇONS =====");
        for (Troncon t : troncons) {
            System.out.println(t);
        }

        // --- Charger un trajet de livraison ---
        String cheminLivraison = "ressources/fichiersXMLPickupDelivery/myDeliverRequest.xml";
        Trajet trajet = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison, mapNoeuds);

        // --- Afficher le trajet ---
        System.out.println("\n===== TRAJET =====");
        if (trajet != null) {
            System.out.println("Nombre de sites accessibles : " + trajet.getSites().size());
            for (Site site : trajet.getSites()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId() +
                                   " | Lat = " + site.getLat() +
                                   " | Lng = " + site.getLng());
            }

            System.out.println("\nSites non accessibles : " + trajet.getSitesNonAccecibles().size());
            for (Site site : trajet.getSitesNonAccecibles()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId());
            }
        }
    }
}
