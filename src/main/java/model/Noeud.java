package model;

public class Noeud {

    // Attributs
    protected long id;
    protected Float latitude;
    protected Float longitude;

    // Constructeur
    public Noeud(long id, Float latitude, Float longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public long getId() {
        return id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    // Méthodes 
    @Override
    public String toString() {
        return "Noeud{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    //Méthode éventuel pour éviter les doublons.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Noeud)) return false;

        Noeud other = (Noeud) obj;
        
        return this.id == other.id;
    }

    //Méthode éventuel pour les tables de haschage
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
