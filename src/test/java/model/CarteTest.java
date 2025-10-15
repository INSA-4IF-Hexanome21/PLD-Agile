package model;

public class CarteTest {

    public static void main(String[] args) {
         System.out.println("===== TEST Carte =====");
        Carte carte =new Carte();
<<<<<<< HEAD
        Livreur livreur = new Livreur(1, "John", "Doe");
=======
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
        Trajet trajet = new Trajet(livreur);
        
        carte.ajouterTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
        carte.supprimerTrajet(trajet);
        System.out.println("Nombre de trajets dans la carte : " + carte.getTrajets().size());
    
    }
}