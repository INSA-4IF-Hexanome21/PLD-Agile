package model;


public class trajetTest {
    public static void main(String[] args) {
        System.out.println("===== TEST Trajet =====");
        Trajet trajet = new Trajet(1);
        System.out.println("Id livreur : " + trajet.getIdLivreur());
        System.out.println("Heure début : " + trajet.getHeureDebut());
        System.out.println("Sites : " + trajet.getSites());
        System.out.println("Durée trajet : " + trajet.getdureeTrajet());
        System.out.println("Heure fin : " + trajet.getHeureFin());
    }
}