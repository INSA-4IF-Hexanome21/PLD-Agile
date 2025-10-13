package model;

public class Delivery extends Site {

    protected Integer numLivraison;
    protected Integer dureeLivraison;

    // Constructeur minimal (lat/lng null)
    public Delivery(long id, Integer numLivraison, Integer dureeRecup) {
        super(id); // lat/lng null
        this.numLivraison = numLivraison;
        this.dureeLivraison = dureeRecup;
    }

    // Constructeur complet (lat/lng fournis)
    public Delivery(long id, Float lat, Float lng, Integer numLivraison, Integer dureeLivraison) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeLivraison = dureeLivraison;
    }

    //Getters / Setters
    public Integer getNumLivraison() {
        return numLivraison;
    }

    public void setNumLivraison(Integer numLivraison) {
        this.numLivraison = numLivraison;
    }

    public Integer getDureeRecup() {
        return dureeLivraison;
    }

    public void setDureeRecup(Integer dureeLivraison) {
        this.dureeLivraison = dureeLivraison;
    }

}
