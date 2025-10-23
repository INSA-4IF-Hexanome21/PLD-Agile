package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import tsp.*;

import model.*;

public class CarteController {
    private Carte carte;

    public CarteController() {
        this.carte = new Carte();
    }

    /**
     * Charge le plan (noeuds et troncons) depuis un fichier XML
     */
    public boolean chargerCarteDepuisXML(String cheminFichier) {
        if (carte == null) {
            carte = new Carte();
        }
        
        HashMap<Long, Noeud> noeuds = GestionnaireXML.chargerPlanNoeuds(cheminFichier);
        carte.setNoeuds(noeuds);
        
        List<Troncon> troncons = GestionnaireXML.chargerPlanTroncons(cheminFichier, noeuds);
        carte.setTroncons(troncons);
        
        if(noeuds.isEmpty() & troncons.isEmpty()){
            return false;
        } else {
            System.out.println("Plan chargé: " + noeuds.size() + " noeuds, " + troncons.size() + " troncons");
            return true;
        }
    }
    
  public synchronized boolean chargerDemandesDepuisXML(String cheminFichierDemandes) {
        // Protection contre appels concurrents
        if (carte == null) {
            carte = new Carte();
        }

        // Avant d'ajouter la nouvelle demande, supprimer l'ancienne pour éviter accumulation
        System.out.println(">>> CarteController: début chargement demandes, effacement des livraisons existantes...");
        this.effacerLivraison();

        Trajet trajet = GestionnaireXML.chargerDemandeLivraisons(
            cheminFichierDemandes, 
            carte.getNoeuds()
        );

        // Ajout du trajet (nouveau)
        carte.ajouterTrajet(trajet);

        // Ajouter les sites du trajet à la carte, mais éviter les doublons par id
        List<Site> sitesTrajet = trajet.getSites();
        HashSet<Long> idsExistants = new HashSet<>();
        for (Site s : carte.getSites()) {
            idsExistants.add(s.getId());
        }

        int ajout = 0;
        for (Site site : sitesTrajet) {
            if (!idsExistants.contains(site.getId())) {
                carte.ajouterSite(site);
                idsExistants.add(site.getId());
                ajout++;
            } else {
                System.out.println(">>> Site déjà présent, id=" + site.getId());
            }
        }

        if(ajout == 0){
            return false;
        } else {
            System.out.println(">>> CarteController: demande chargée, sites ajoutés=" + ajout + ", total sites=" + carte.getSites().size());
            return true;
        }
    }


  // --- modifications dans controller/CarteController.java ---

    public void calculerTournee() throws Exception {
        // Vérifications préalables
        if (this.getCarte() == null) {
            throw new IllegalStateException("Carte non chargée");
        }
        if (this.getCarte().getSites() == null || this.getCarte().getSites().isEmpty()) {
            throw new IllegalStateException("Aucune demande / sites non chargés dans la carte");
        }

        // Effacer les calculs précédents pour éviter d'utiliser des structures obsolètes
        this.effacerCalcul();

        // chercher entrepot
        Entrepot e = null;
        for (Site site : this.getCarte().getSites()) {
            if (site instanceof Entrepot) {
                e = (Entrepot) site;
                break;
            }
        }
        if (e == null) {
            throw new IllegalStateException("Entrepôt introuvable dans la carte");
        }

        // creer graphe total et calculer chemins minimaux
        GrapheTotal gt = creerGrapheTotal(this.getCarte(), e.getId());
        this.chercherCheminsMin(gt, this.getCarte().getSites());
    }

    /**
     * Efface les résultats du calcul précédent :
     * - vide les troncons et la durée des trajets existants
     * - remet à null les heures d'arrivée/départ et numPassage des sites
     * - marque la carte comme sans calcul
     */
    public void effacerCalcul() {
        // --- commentaires en français comme demandé ---
        // Réinitialiser les trajectoires calculées
        if (this.carte == null) return;

        // Effacer données de chaque trajet existant
        if (this.carte.getTrajets() != null) {
            for (Trajet t : this.carte.getTrajets()) {
                try {
                    t.setTroncons(new ArrayList<Troncon>());
                    // setdureeTrajet attend un float ; on appelle avec 0f puis on remet heureFin à null si besoin
                    // mais on préfère définir la durée à 0 et l'heure de fin via setdureeTrajet
                    t.setdureeTrajet(0f);
                } catch (Exception ex) {
                    // protège contre implémentations inattendues
                    System.err.println(">>> effacerCalcul: erreur lors de la réinitialisation du trajet : " + ex.getMessage());
                }

                // Remettre à null les heures et numPassage des sites du trajet
                if (t.getSites() != null) {
                    for (Site s : t.getSites()) {
                        try {
                            // méthodes attendues sur Site : setArriveeHeure, setDepartHeure, setNumPassage
                            s.setArriveeHeure(null);
                            s.setDepartHeure(null);
                            s.setNumPassage(null);
                        } catch (Exception ex2) {
                            // si la classe Site n'a pas exactement ces méthodes, log pour debugging
                            System.err.println(">>> effacerCalcul: impossible de réinitialiser site " + s.getId() + " : " + ex2.getMessage());
                        }
                    }
                }
            }
        }

        // on ne supprime pas les trajets eux-mêmes (conserver la structure), mais on les remet à l'état non calculé.
        System.out.println(">>> CarteController: calculs précédents effacés.");
    }

    /**
     * Efface la livraison (demandes) : supprime les trajets et retire des sites ajoutés par les trajets.
     * On conserve la carte (noeuds/troncons) mais on retire les sites liés à la/aux demande(s).
     */
    public void effacerLivraison() {
        if (this.carte == null) return;

        // Rassembler tous les sites référencés dans les trajets pour les supprimer de la carte
        List<Site> sitesASupprimer = new ArrayList<>();
        if (this.carte.getTrajets() != null) {
            for (Trajet t : this.carte.getTrajets()) {
                if (t.getSites() != null) {
                    sitesASupprimer.addAll(t.getSites());
                }
            }
        }

        // Supprimer les sites (évite ConcurrentModification en créant une copie)
        if (!sitesASupprimer.isEmpty()) {
            for (Site s : sitesASupprimer) {
                try {
                    this.carte.supprimerSite(s);
                } catch (Exception ex) {
                    System.err.println(">>> effacerLivraison: impossible de supprimer site " + s.getId() + " : " + ex.getMessage());
                }
            }
        }

        // Supprimer tous les trajets
        try {
            this.carte.getTrajets().clear();
        } catch (Exception ex) {
            System.err.println(">>> effacerLivraison: erreur lors de la suppression des trajets : " + ex.getMessage());
        }

        System.out.println(">>> CarteController: livraisons précédentes effacées.");
    }

    
    /**
     * Génère le JSON complet de la carte avec noeuds, troncons et sites
     */
 public String getCarteJSON() {
    System.out.println(">>> getCarteJSON appelé <<<");

    StringBuilder json = new StringBuilder();
    json.append("{");

    // -- Noeuds
    json.append("\"noeuds\":[");
    boolean firstNoeud = true;
    for (Noeud n : carte.getNoeuds().values()) {
        if (!firstNoeud) json.append(",");
        firstNoeud = false;
        json.append(String.format(Locale.US,"{\"id\":%d,\"lat\":%f,\"lng\":%f}",
                n.getId(), n.getLatitude(), n.getLongitude()));
    }
    json.append("]");

    // -- Troncons
    json.append(",\"troncons\":[");
    boolean firstTroncon = true;
    for (Troncon t : carte.getTroncons()) {
        if (!firstTroncon) json.append(",");
        firstTroncon = false;
        json.append(String.format("{\"from\":%d,\"to\":%d}",
                t.getOrigine().getId(), t.getDestination().getId()));
    }
    json.append("]");

    // -- Sites
    json.append(",\"sites\":[");
    boolean firstSite = true;
    for (Site s : carte.getSites()) {
        if (!firstSite) json.append(",");
        firstSite = false;
        Integer numLivraison = null;
        if (s instanceof Depot ) numLivraison = ((Depot) s).getNumLivraison();
        else if (s instanceof Collecte ) numLivraison = ((Collecte) s).getNumLivraison();

        try {
            double lat = s.getLatitude();
            double lng = s.getLongitude();
            json.append(String.format(Locale.US,
                "{\"id\":%d,\"lat\":%f,\"lng\":%f,\"type\":\"%s\"",
                s.getId(), lat, lng, s.getTypeSite()));

            if (s.getDepartHeure() != null) {
                json.append(String.format(",\"depart\":\"%s\"", s.getDepartHeure().toString()));
            }
            if (s.getArriveeHeure() != null) {
                json.append(String.format(",\"arrivee\":\"%s\"", s.getArriveeHeure().toString()));
            }
            if (numLivraison != null) {
                json.append(String.format(",\"numLivraison\":%d", numLivraison));
            }
            if (s.getNumPassage() != null) {
                json.append(String.format(",\"numPassage\":%d", s.getNumPassage()));
            }
            json.append("}");
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du site " + s.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    json.append("]");

    // -- Trajets (concaténation de tous les troncons de tous les trajets)
    json.append(",\"trajets\":[");
    boolean firstTrajetEntry = true;
    for (Trajet t : carte.getTrajets()) {
        for (Troncon tr : t.getTroncons()) {
            if (!firstTrajetEntry) json.append(",");
            firstTrajetEntry = false;
            json.append(String.format("{\"from\":%d,\"to\":%d}",
                    tr.getOrigine().getId(), tr.getDestination().getId()));
        }
    }
    json.append("]");

    json.append("}");
    return json.toString();
}

    public Carte getCarte() {
        return carte;
    }

    public GrapheTotal creerGrapheTotal(Carte carte, long idEntrepot){
        HashMap<Long, Noeud> noeuds = carte.getNoeuds();
        List<Troncon> troncons = carte.getTroncons();
        int nbSommets = noeuds.size();

        GrapheTotal gt = new GrapheTotal(nbSommets, troncons, noeuds, idEntrepot);
        //gt.printGraphe();
        return gt;
    }

    public void chercherCheminsMin(GrapheTotal gt, List<Site> sites){
        gt.RechercheDijkstra(sites);
        GrapheLivraison gl = new GrapheLivraison(carte.getSites().size(), gt.getMapDistances());
        gl.setContrainteHashMap(gt.getContrainteHashMap());
        TSP tsp = new TSP2();
        tsp.chercheSolution(60000, gl);

        List<Integer> solution = new ArrayList<Integer>();
        for (int i=0; i<gl.getNbSommets(); i++) {
            solution.add(gl.getIdFromIndex(tsp.getSolution(i)));
        }
        solution.add(solution.get(0));

        // Reconstruction du chemin complet : vérifier que getCheminComplet ne renvoie pas null
        List<Integer> cheminComplet = gt.getCheminComplet(solution);
        if (cheminComplet == null) {
            // Fournir un message utile pour le debug (indices, taille des maps, etc.)
            String msg = "Erreur: getCheminComplet a renvoyé null. Solution: " + solution
                         + " | mapCheminsMin keys: " + (gt.getCheminsMin() != null ? gt.getCheminsMin().keySet() : "null")
                         + " | mapDistances keys: " + (gt.getMapDistances() != null ? gt.getMapDistances().keySet() : "null");
            System.err.println(">>> chercherCheminsMin: " + msg);
            throw new IllegalStateException(msg);
        }

        List<Long> cheminCompletConverti = gt.convertirCheminComplet(cheminComplet);
        majTrajet(carte, gt, cheminCompletConverti, solution);
    }


    public void majTrajet(Carte carte, GrapheTotal gt, List<Long> cheminComplet, List<Integer> solution){
        carte.majTrajetDepuisChemin(gt,cheminComplet,solution,carte.getTrajets().get(0));
        //System.out.println(carte.getTrajets().get(0));
    }
}
