package model;

public class Entrepot extends Site {
<<<<<<< HEAD

    public Entrepot(String id, String lat, String lng) {
        super(id, lat, lng);
    }

=======
    
    // Constructeur minimal (lat/lng null)
    public Entrepot(long id) {
        super(id); // lat/lng null
    }

    // Constructeur complet (lat/lng fournis)
    public Entrepot(long id, Float lat, Float lng) {
        super(id, lat, lng);
    }

    @Override
    public String getTypeSite() {
        return "entrepot";
    }
>>>>>>> 785a18d5834ff9f222bdc65d4e5e25e05819b4a2
}
