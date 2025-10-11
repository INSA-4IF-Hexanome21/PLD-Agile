package main.java.model;

public class Delivery extends Site {

    protected Integer numLivraison;
    protected Integer dureeLivraison;

    public Delivery(String id, String lat, String lng, Integer numLivraison, Integer dureeLivraison) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeLivraison = dureeLivraison;
    }

}
