package model;

public class CarteTest {

    public static void main(String[] args) {
         System.out.println("===== TEST Carte =====");
        Carte carte =new Carte();
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
        Trajet trajet = new Trajet(livreur);
        
        carte.ajouterTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
        carte.supprimerTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
    
    }
}