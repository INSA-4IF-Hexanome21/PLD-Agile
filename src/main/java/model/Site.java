package model;

import java.time.LocalTime;

public abstract class Site {

    protected long id;
    protected float lat;
    protected float lng;
    protected LocalTime depart_heure = null;
    protected LocalTime arrivee_heure = null;

    public Site(long id, float lat, float lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public long getId() {
        return id;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public LocalTime getDepartHeure() {
        return depart_heure;
    }

    public LocalTime getArriveeHeure() {
        return arrivee_heure;
    }

}
