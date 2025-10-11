package main.java.model;

import java.time.LocalTime;

public abstract class Site {

    protected String id;
    protected String lat;
    protected String lng;
    protected LocalTime depart_heure = null;
    protected LocalTime arrivee_heure = null;

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

    public LocalTime getDepartHeure() {
        return depart_heure;
    }

    public LocalTime getArriveeHeure() {
        return arrivee_heure;
    }

}
