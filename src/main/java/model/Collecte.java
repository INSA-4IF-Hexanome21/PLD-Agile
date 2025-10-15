package model;

public class Collecte extends Site {

    //Attributs
    protected Integer numLivraison;
    protected Integer dureeRecup;

    // Constructeur minimal (lat/lng null)
    public Collecte(long id, Integer numLivraison, Integer dureeRecup) {
        super(id); // lat/lng null
        this.numLivraison = numLivraison;
        this.dureeRecup = dureeRecup;
    }

    // Constructeur complet (lat/lng fournis)
    public Collecte(long id, Float lat, Float lng, Integer numLivraison, Integer dureeRecup) {
        super(id, lat, lng);
        this.numLivraison = numLivraison;
        this.dureeRecup = dureeRecup;
    }

    // Getters / Setters 
    public Integer getNumLivraison() {
        return numLivraison;
    }

    public void setNumLivraison(Integer numLivraison) {
        this.numLivraison = numLivraison;
    }

    public Integer getDureeRecup() {
        return dureeRecup;
    }

    public void setDureeRecup(Integer dureeRecup) {
        this.dureeRecup = dureeRecup;
    }

    @Override
    public String getTypeSite() {
        return "collecte";
    }
}

