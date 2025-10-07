package modeles;

public class Pickup extends Site {

    protected Integer numLivraison;
    protected Integer dureeRecup;
    protected Timestamp arrivee_heure;

    public Pickup(String id, String lat, String lng, Timestamp depart_heure) {
        super(id, lat, lng, depart_heure);
    }

    @Override
    public void ajouter() {

    }

}