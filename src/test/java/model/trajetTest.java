package model;


public class trajetTest {
    public static void main(String[] args) {
        System.out.println("===== TEST Trajet =====");
        Livreur livreur = new Livreur(0, "Petit", "Bobert")
        Trajet trajet = new Trajet(livreur);
        System.out.println("Livreur : " + trajet.getLivreur());
        System.out.println("Heure début : " + trajet.getHeureDebut());
        System.out.println("Sites : " + trajet.getSites());
        System.out.println("Durée trajet : " + trajet.getdureeTrajet());
        System.out.println("Heure fin : " + trajet.getHeureFin());
    }
}