package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class GestionnaireXML {
    // Attributs
    private List<Noeud> noeuds;
    private List<Troncon> troncons;

    // Constructeur
    public GestionnaireXML(String cheminFichier) {
        this.noeuds = new ArrayList<>();
        this.troncons = new ArrayList<>();
        chargerFichier(cheminFichier);
    }

    // Getter
    public List<Noeud> getNoeuds() {
        return noeuds;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    // Méthodes
    private void chargerFichier(String cheminFichier) {
        Map<Long, Noeud> mapNoeuds = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(cheminFichier));
            document.getDocumentElement().normalize();

            // Charger les noeuds
            NodeList noeudsXML = document.getElementsByTagName("noeud");
            for (int i = 0; i < noeudsXML.getLength(); i++) {
                Element elem = (Element) noeudsXML.item(i);
                long id = Long.parseLong(elem.getAttribute("id"));
                float latitude = Float.parseFloat(elem.getAttribute("latitude"));
                float longitude = Float.parseFloat(elem.getAttribute("longitude"));

                Noeud noeud = new Noeud(id, latitude, longitude);
                noeuds.add(noeud);
                mapNoeuds.put(id, noeud);
            }

            // Charger les tronçons
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
                    Troncon troncon = new Troncon(nomRue, longueur, origine, destination);
                    troncons.add(troncon);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
