package model;


public class trajetTest {
    public static void main(String[] args) {
        System.out.println("===== TEST Trajet =====");
        Livreur livreur = new Livreur(0, "Petit", "Bobert");
        Trajet trajet = new Trajet(livreur);
<<<<<<< HEAD
        System.out.println("Id livreur : " + trajet.getLivreur());
=======
        System.out.println("Livreur : " + trajet.getLivreur());
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
        System.out.println("Heure début : " + trajet.getHeureDebut());
        System.out.println("Sites : " + trajet.getSites());
        System.out.println("Durée trajet : " + trajet.getdureeTrajet());
        System.out.println("Heure fin : " + trajet.getHeureFin());
    }
}