package modeles;

public abstract class Site {

    protected String id;
    protected String lat;
    protected String lng;

    public Site(String id, String lat, String lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
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

}