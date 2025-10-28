package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import java.io.FileWriter;
import java.io.IOException;

public class Trajet {

    // Attributs
    private Livreur livreur;
    private List<Site> sites;
    private List<Site> nonAccessibles;
    private List<Troncon> troncons;
    private Float dureeTrajet;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    private List<Long> cheminComplet;
    private List<Long> solution;


    // Constructeur complet
    public Trajet(Livreur livreur) {
        this.livreur = livreur;
        this.sites = new ArrayList<>();
        this.troncons = new ArrayList<>();
        this.nonAccessibles = new ArrayList<>();
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }
    // Constructeur incomplet
    public Trajet() {
        this.livreur = null;
        this.sites = new ArrayList<>();
        this.troncons = new ArrayList<>();
        this.nonAccessibles = new ArrayList<>();
        this.dureeTrajet = null;
        this.heureDebut = LocalTime.of(8, 00); //On part toujours de l'entrpôt à 8h
        this.heureFin = null;
    }

    // Getters et Setters

    public Livreur getLivreur() {
        return this.livreur;
    }

    public void setLivreur(Livreur new_livreur){
        this.livreur = new_livreur;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public Float getdureeTrajet() {
        return dureeTrajet;
    }

    public void setdureeTrajet(float duree) {
        this.dureeTrajet = duree;
        float heure_fin = 8 + duree;
        this.heureFin = LocalTime.of((int)heure_fin,(int)((duree%1)*60));
    }

    public List<Site> getSites(){
        return sites;
    }

    public void addSite(Site site){
        sites.add(site);
    }

    public void removeSite(Site site) {
        sites.remove(site);
    }

    public List<Site> getSitesNonAccessibles(){
        return nonAccessibles;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }

    public void setSolution(List<Long> solution) {
        this.solution = solution;
    }

    public void setCheminComplet(List<Long> cheminComplet) {
        this.cheminComplet = cheminComplet;
    }

    public List<Long> getSolution() {
        return this.solution;
    }

    public List<Long> getCheminComplet() {
        return this.cheminComplet;
    }

    public Site getSite(Long id){
        Site siteTrouve = null;
        for(Site site: sites){
            if(site.getId() == id){
                siteTrouve = site;
                break;
            }
        }
        return siteTrouve;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trajet{\n");
        
        // Informations de base
        sb.append("  Livreur: ").append(livreur != null ? livreur : "Non assigné").append("\n");
        sb.append("  Heure de début: ").append(heureDebut).append("\n");
        sb.append("  Heure de fin: ").append(heureFin != null ? heureFin : "Non calculée").append("\n");
        sb.append("  Durée du trajet: ").append(dureeTrajet != null ? String.format("%.2f heures", dureeTrajet) : "Non calculée").append("\n");
        
        // Nombre de sites
        sb.append("  Nombre de sites : ").append(sites.size()+ "\n");
        
        // Nombre de Troncons
        sb.append(troncons);
        
        sb.append("\n}");
        return sb.toString();
    }

    public void genererFeuilleDeRoute(){

        LocalDate localDate = LocalDate.now(ZoneId.of("Europe/Paris"));//For reference
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = localDate.format(formatter);

        String rueActuelle = null;
        Float longueur = null;

        String data = "Feuille de route " + date + "\n\n";
        data += this.livreur + "\n";
        data += "Trajet à effectuer : \n\n";
        data += "Départ de l'entrepôt (" + troncons.get(0).getOrigine().getId() + ") à 08:00h\n";
        
        for(Troncon troncon : this.troncons){
            if(rueActuelle != null && !(troncon.getNomRue().equals(rueActuelle))){
                data += rueActuelle + " sur " + longueur.intValue() + " m\n";
            }
            if(!(troncon.getNomRue().equals(rueActuelle))){
                rueActuelle = troncon.getNomRue();
                if(rueActuelle.equals("")){
                    rueActuelle = "Rue non référencée";
                }
                longueur = troncon.getLongueur();
            }
            else{
                longueur += troncon.getLongueur();
            }
            
            //On vérifie si le lieu de destination est un site
            Site site = getSite(troncon.getDestination().getId());
            if( site != null && !(site instanceof Entrepot)){
                data += rueActuelle + " sur " + longueur.intValue() + " m\n";
                data += "Arrivée sur le lieu de " + site.getTypeSite() + " (" + site.getId()+") à " + site.getArriveeHeure() + "\n";
                data += "Départ du lieu de " + site.getTypeSite() + " (" + site.getId()+") à " + site.getDepartHeure() + "\n";
                rueActuelle = null;
                longueur = 0f;
            }
        }
        data += rueActuelle + " sur " + longueur.intValue() + " m\n";
        Entrepot entrepot = (Entrepot)getSite(troncons.get(0).getOrigine().getId());
        data += "Arrivée à l'entrepôt (" + troncons.get(0).getOrigine().getId() + ") à " + entrepot.getArriveeHeure() + "\n\n";
        data += "Temps total du trajet : " + this.dureeTrajet.intValue() + ":" + (int)((this.dureeTrajet%1)*60) + "h";

        try {
            // create a FileWriter object with the file name
            FileWriter writer = new FileWriter(this.livreur.getNom() + "_" + this.livreur.getPrenom() + ".txt");

            // write the string to the file
            writer.write(data);

            // close the writer
            writer.close();

            // System.out.println("Successfully wrote text to file.");

        } catch (IOException e) {
            System.out.println("Erreur dans l'écriture du fichier");
            e.printStackTrace();
        }
    }
}