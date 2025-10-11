package test.java.model;

import main.java.model.Carte;
import main.java.model.Trajet;

public class CarteTest {

    public static void testCarte() {
        Carte carte = new Carte();
        Trajet trajet = new Trajet(1);

        carte.ajouterTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
        carte.supprimerTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
    
    }
}
