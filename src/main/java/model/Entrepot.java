package model;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Entrepot extends Site {

    private List<LocalTime> heuresArriveesTrajets;
    
    // Constructeur minimal (lat/lng null)
    public Entrepot(long id) {
        super(id); // lat/lng null
        this.heuresArriveesTrajets = new ArrayList<LocalTime>();
    }

    // Constructeur complet (lat/lng fournis)
    public Entrepot(long id, Float lat, Float lng) {
        super(id, lat, lng);
        this.heuresArriveesTrajets = new ArrayList<LocalTime>();
    }

    public void changeHeures(List<LocalTime> heures){
        this.heuresArriveesTrajets = heures;
    }

    public List<LocalTime> getHeures(){
        return this.heuresArriveesTrajets;
    }

    @Override
    public String getTypeSite() {
        return "entrepot";
    }
}
