package controller;

import org.w3c.dom.*;

import model.Delivery;
import model.Entrepot;
import model.Noeud;
import model.Pickup;
import model.Trajet;
import model.Troncon;

import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class GestionnaireXML {

    // Méthode pour charger un plan et renvoyer la HashMap de noeuds
    public static HashMap<Long, Noeud> chargerPlanNoeuds(String cheminFichier) {
        HashMap<Long, Noeud> mapNoeuds = new HashMap<>();
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

    // Méthode pour charger un plan et renvoyer la liste de tronçons
    public static List<Troncon> chargerPlanTroncons(String cheminFichier, HashMap<Long, Noeud> mapNoeuds) {
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

    // Méthode pour charger une demande de livraison et renvoyer le trajet à effectuer
    public static Trajet chargerDemandeLivraisons(String cheminFichier, HashMap<Long, Noeud> mapNoeuds) {
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
            if (noeudEntrepot == null) {
                Entrepot entrepot = new Entrepot(idEntrepot);
                trajet.getSitesNonAccessibles().add(entrepot);
            } else {
                Entrepot entrepot = new Entrepot(
                        idEntrepot,
                        noeudEntrepot.getLatitude(),
                        noeudEntrepot.getLongitude()
                );
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s");
                    LocalTime heure = LocalTime.parse(heureDepart, formatter);
                    entrepot.setDepartHeure(heure);
                }
                catch (Exception e1) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");
                        LocalTime heure = LocalTime.parse(heureDepart, formatter);
                        entrepot.setDepartHeure(heure);
                    } catch (Exception e2) {
                        System.err.println("Impossible de lire l'heure de départ : " + heureDepart);
                        entrepot.setDepartHeure(LocalTime.of(8, 0)); // valeur par défaut
                    }
                }
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
                int numLivraison = i;

                // Pickup
                Noeud noeudPickup = trouverNoeud(adresseEnlevement, mapNoeuds);
                Pickup pickup;
                if (noeudPickup == null) {
                    pickup = new Pickup(idLivraison, numLivraison, dureeEnlevement);  
                } else {
                    pickup = new Pickup(
                            idLivraison,
                            noeudPickup.getLatitude(),
                            noeudPickup.getLongitude(),
                            numLivraison,
                            dureeEnlevement
                    );
                }

                // Delivery
                Noeud noeudDelivery = trouverNoeud(adresseLivraison, mapNoeuds);
                Delivery delivery;
                if (noeudDelivery == null) {
                    delivery = new Delivery(idEnlevement, numLivraison, dureeLivraison);
                    trajet.getSitesNonAccessibles().add(delivery);
                } else {
                    delivery = new Delivery(
                            idEnlevement,
                            noeudDelivery.getLatitude(),
                            noeudDelivery.getLongitude(),
                            numLivraison,
                            dureeLivraison
                    );
                }
                if( noeudDelivery == null || noeudPickup == null) {
                    trajet.getSitesNonAccessibles().add(pickup);
                    trajet.getSitesNonAccessibles().add(delivery);
                }
                else{
                    trajet.getSites().add(pickup);
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
    private static Noeud trouverNoeud(String idStr, HashMap<Long, Noeud> mapNoeuds) {
        try {
            long id = Long.parseLong(idStr);
            return mapNoeuds.get(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
