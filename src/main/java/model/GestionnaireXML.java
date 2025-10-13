package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalTime;
import java.util.*;

public class GestionnaireXML {

    //Méthode pour charger un plan et renvoyer la Map de noeuds
    public static Map<Long, Noeud> chargerPlanNoeuds(String cheminFichier) {
        Map<Long, Noeud> mapNoeuds = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(cheminFichier));
            document.getDocumentElement().normalize();

            NodeList noeudsXML = document.getElementsByTagName("noeud");
            for (int i = 0; i < noeudsXML.getLength(); i++) {
                Element elem = (Element) noeudsXML.item(i);
                long id = Long.parseLong(elem.getAttribute("id"));
                float lat = Float.parseFloat(elem.getAttribute("latitude"));
                float lng = Float.parseFloat(elem.getAttribute("longitude"));
                mapNoeuds.put(id, new Noeud(id, lat, lng));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des noeuds : " + e.getMessage());
            e.printStackTrace();
        }
        return mapNoeuds;
    }
    //Méthode pour charger un plan et renvoyer la liste de tronçons
    public static List<Troncon> chargerPlanTroncons(String cheminFichier, Map<Long, Noeud> mapNoeuds) {
        List<Troncon> troncons = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(cheminFichier));
            document.getDocumentElement().normalize();

            NodeList tronconsXML = document.getElementsByTagName("troncon");
            for (int i = 0; i < tronconsXML.getLength(); i++) {
                Element elem = (Element) tronconsXML.item(i);
                long idOrigine = Long.parseLong(elem.getAttribute("origine"));
                long idDestination = Long.parseLong(elem.getAttribute("destination"));
                float longueur = Float.parseFloat(elem.getAttribute("longueur"));
                String nomRue = elem.getAttribute("nomRue");

                Noeud origine = mapNoeuds.get(idOrigine);
                Noeud destination = mapNoeuds.get(idDestination);

                if (origine != null && destination != null) {
                    troncons.add(new Troncon(nomRue, longueur, origine, destination));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des tronçons : " + e.getMessage());
            e.printStackTrace();
        }
        return troncons;
    }

    //Méthode pour charger une demande de livraison et renvoyer le trajet a effectuert ainsi que la liste de sites qui n'existe pas
    public static Trajet chargerDemandeLivraisons(String cheminFichier, Map<Long, Noeud> mapNoeuds) {
        Trajet trajet = new Trajet();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(cheminFichier));
            document.getDocumentElement().normalize();

            // Entrepôt
            Element entrepotElem = (Element) document.getElementsByTagName("entrepot").item(0);
            String adresseEntrepot = entrepotElem.getAttribute("adresse");
            String heureDepart = entrepotElem.getAttribute("heureDepart");
            long idEntrepot = Long.parseLong(adresseEntrepot);

            Noeud noeudEntrepot = trouverNoeud(adresseEntrepot, mapNoeuds);
            if(noeudEntrepot == null){
                Entrepot entrepot = new Entrepot(idEntrepot);
                trajet.getSitesNonAccecibles().add(entrepot);
            }
            else{
                Entrepot entrepot = new Entrepot(
                    idEntrepot,
                    noeudEntrepot.getLatitude(),
                    noeudEntrepot.getLongitude()
                );
                entrepot.setDepartHeure(LocalTime.parse(heureDepart));
                trajet.getSites().add(entrepot);
                }
            

            // Livraisons
            NodeList livraisonsXML = document.getElementsByTagName("livraison");
            for (int i = 0; i < livraisonsXML.getLength(); i++) {
                Element elem = (Element) livraisonsXML.item(i);

                String adresseEnlevement = elem.getAttribute("adresseEnlevement");
                String adresseLivraison = elem.getAttribute("adresseLivraison");
                long idEnlevement = Long.parseLong(adresseEnlevement);
                long idLivraison = Long.parseLong(adresseLivraison);
                int dureeEnlevement = Integer.parseInt(elem.getAttribute("dureeEnlevement"));
                int dureeLivraison = Integer.parseInt(elem.getAttribute("dureeLivraison"));
                int numLivraison = i ;

                // Pickup
                Noeud noeudPickup = trouverNoeud(adresseEnlevement, mapNoeuds);
                Pickup pickup;
                if(noeudPickup == null){
                    pickup = new Pickup(
                        idLivraison, 
                        numLivraison, 
                        dureeEnlevement
                    );
                    trajet.getSitesNonAccecibles().add(pickup);
                }
                else{
                    pickup = new Pickup(
                        idLivraison,
                        noeudPickup.getLatitude(),
                        noeudPickup.getLongitude(),
                        numLivraison,
                        dureeEnlevement
                    );
                    trajet.getSites().add(pickup);
                }

                // Delivery
                Noeud noeudDelivery = trouverNoeud(adresseLivraison, mapNoeuds);
                Delivery delivery;
                if(noeudDelivery == null){
                    delivery = new Delivery(
                        idEnlevement,
                        numLivraison,
                        dureeLivraison
                    );
                    trajet.getSitesNonAccecibles().add(delivery);
                }
                else{
                    delivery = new Delivery(
                        idEnlevement,
                        noeudDelivery.getLatitude(),
                        noeudDelivery.getLongitude(),
                        numLivraison,
                        dureeLivraison
                    );
                    trajet.getSites().add(delivery);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la demande de livraisons : " + e.getMessage());
            e.printStackTrace();
        }
        return trajet;
    }

    // --- Méthode utilitaire ---
    private static Noeud trouverNoeud(String idStr, Map<Long, Noeud> mapNoeuds) {
        try {
            long id = Long.parseLong(idStr);
            return mapNoeuds.get(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
