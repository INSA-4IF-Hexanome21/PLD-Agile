package tests;

import modeles.Carte;

public class CarteTest {

    public static void main(String[] args) {
        Carte carte =new Carte();
        Trajet trajet = new Trajet(1);

        carte.ajouterTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
        carte.supprimerTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
    
    }
}
