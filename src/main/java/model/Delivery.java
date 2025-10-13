package model;

public class Delivery extends Site {

    protected Integer numLivraison;
    protected Integer dureeLivraison;

    public Delivery(long id, float lat, float lng, Integer numLivraison, Integer dureeLivraison) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeLivraison = dureeLivraison;
    }

}
