package modeles;

public class Pickup extends Site {

    protected Integer numLivraison;
    protected Integer dureeRecup;

    public Pickup(String id, String lat, String lng, Integer numLivraison, Integer dureeRecup) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeRecup = dureeRecup;
    }

}