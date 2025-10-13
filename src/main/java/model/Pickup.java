package model;

public class Pickup extends Site {

    protected Integer numLivraison;
    protected Integer dureeRecup;

    public Pickup(long id, float lat, float lng, Integer numLivraison, Integer dureeRecup) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeRecup = dureeRecup;
    }

}
