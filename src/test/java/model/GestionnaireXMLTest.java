package model;


public class GestionnaireXMLTest {

    public static void main(String[] args) {
        // Chemin vers ton fichier XML local
        String cheminFichier = "../../../ressources/fichiersXMLPickupDelivery/petitPlan.xml";

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
    }
}
