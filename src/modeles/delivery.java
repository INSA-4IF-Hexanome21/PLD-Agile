package modeles;

public class Delivery extends Site {

    protected Integer numLivraison;
    protected Integer dureeLivraison;

    public Delivery(String id, String lat, String lng, Timestamp depart_heure) {
        super(id, lat, lng, depart_heure);
    }

    @Override
    public void ajouter() {
        
    }

}