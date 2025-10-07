package modeles;

public abstract class Site {

    protected String id;
    protected String lat;
    protected String lng;
    protected Timestamp depart_heure = null;
    protected Timestamp arrivee_heure = null;

    public Site(String id, String lat, String lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public Timestamp getDepartHeure() {
        return depart_heure;
    }

    public Timestamp getArriveeHeure() {
        return arrivee_heure;
    }

}