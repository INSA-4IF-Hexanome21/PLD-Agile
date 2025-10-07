package modeles;

public abstract class Site {

    protected String id;
    protected String lat;
    protected String lng;
    protected Timestamp depart_heure;

    public Site(String id, String lat, String lng, Timestamp depart_heure) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.depart_heure = depart_heure;
    }

    public abstract void ajouter();
    public abstract void supprimer();

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

}