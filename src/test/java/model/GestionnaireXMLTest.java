package model;

<<<<<<< HEAD
=======
import java.util.HashMap;
import java.util.List;

import controller.GestionnaireXML;
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2

public class GestionnaireXMLTest {

    public static void main(String[] args) {
<<<<<<< HEAD
        System.out.println("===== TEST Gestionaire =====");
        // Chemin vers ton fichier XML local
        String cheminFichier = "ressources/fichiersXMLPickupDelivery/petitPlan.xml";

        // Création du gestionnaire XML et chargement des données
        GestionnaireXML gestionnaire = new GestionnaireXML(cheminFichier);

        // Affichage de tous les noeuds
        System.out.println("===== NOEUDS =====");
        for (Noeud n : gestionnaire.getNoeuds()) {
            System.out.println(n);
        }

        // Affichage de tous les tronçons
        System.out.println("\n===== TRONÇONS =====");
        for (Troncon t : gestionnaire.getTroncons()) {
            System.out.println(t);
        }
=======
        System.out.println("===== TEST Gestionnaire XML =====");

        // Chemin vers le fichier plan
        String cheminPlan = "ressources/fichiersXMLCollecteDepot/petitPlan.xml";

        //Charger la HashMap de noeuds
        HashMap<Long, Noeud> mapNoeuds = GestionnaireXML.chargerPlanNoeuds(cheminPlan);
        System.out.println("===== NOEUDS =====");
        for (Noeud n : mapNoeuds.values()) {
            System.out.println(n);
        }

        //Charger la liste des tronçons
        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminPlan, mapNoeuds);
        System.out.println("\n===== TRONÇONS =====");
        for (Troncon t : troncons) {
            System.out.println(t);
        }

        //Charger un trajet de livraison
        String cheminLivraison = "ressources/fichiersXMLCollecteDepot/myDeliverRequest.xml";
        Trajet trajet = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison, mapNoeuds);

        //  Afficher le trajet 
        System.out.println("\n===== TRAJET =====");
        if (trajet != null) {
            System.out.println("Nombre de sites accessibles : " + trajet.getSites().size());
            for (Site site : trajet.getSites()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId() +
                                   " | Lat = " + site.getLat() +
                                   " | Lng = " + site.getLng());
            }

            System.out.println("\nSites non accessibles : " + trajet.getSitesNonAccessibles().size());
            for (Site site : trajet.getSitesNonAccessibles()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId());
            }
        }

        cheminLivraison = "ressources/fichiersXMLCollecteDepot/demandePetit1.xml";
        trajet = GestionnaireXML.chargerDemandeLivraisons(cheminLivraison, mapNoeuds);

        //  Afficher le trajet
        System.out.println("\n===== TRAJET =====");
        if (trajet != null) {
            System.out.println("Nombre de sites accessibles : " + trajet.getSites().size());
            for (Site site : trajet.getSites()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId() +
                                   " | Lat = " + site.getLat() +
                                   " | Lng = " + site.getLng());
            }

            System.out.println("\nSites non accessibles : " + trajet.getSitesNonAccessibles().size());
            for (Site site : trajet.getSitesNonAccessibles()) {
                System.out.println(site.getClass().getSimpleName() +
                                   " | ID = " + site.getId());
            }
        }
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
    }
}
