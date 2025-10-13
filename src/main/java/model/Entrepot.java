package model;

public class Entrepot extends Site {
    
    // Constructeur minimal (lat/lng null)
    public Entrepot(long id) {
        super(id); // lat/lng null
    }

    // Constructeur complet (lat/lng fournis)
    public Entrepot(long id, Float lat, Float lng) {
        super(id, lat, lng);
    }

}
