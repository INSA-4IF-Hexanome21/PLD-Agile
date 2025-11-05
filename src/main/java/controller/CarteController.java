package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.LocalTime;

import tsp.*;
import controller.command.*;
import model.*;
import model.utils.CarteUtils;

public class CarteController {
    private Carte carte;
    private DemandeLivraison demandeLivraison;
    private GrapheTotal gt;
    private final ListOfCommands history;
    private Configuration config;

    public CarteController() {
        this.carte = new Carte();
        this.history = new ListOfCommands();
        this.config = new Configuration();
        this.gt = null;
    }

    // Getters et Setters
    public void setGrapheTotal(GrapheTotal gt) {
        this.gt = gt;
    }

    public GrapheTotal getGrapheTotal() {
        return this.gt;
    }

    // Méthodes
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
        carte.resetTournee();

        // Avant d'ajouter la nouvelle demande, supprimer l'ancienne pour éviter accumulation
        System.out.println(">>> CarteController: début chargement demandes, effacement des livraisons existantes...");
        this.effacerLivraison();

        demandeLivraison = GestionnaireXML.chargerDemandeLivraisons(
            cheminFichierDemandes, 
            carte.getNoeuds()
        );
        if(demandeLivraison == null){
            throw new NullPointerException("Un site de la demande de livraison n'est pas disponible sur le plan actuellement chargé");
        }

        // Ajouter les sites de la demande de livraison à la carte, mais éviter les doublons par id
        List<Site> sitesTrajet = demandeLivraison.getSites();
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

  //Assigner les livreurs

    public void assignerLivreurs(HashMap<String, List<String>> assignation) {
        // Avant d'ajouter la nouvelle demande, supprimer l'ancienne pour éviter accumulation
        System.out.println(">>> CarteController: début du chargement de l'assignation des livraisons");

        if(assignation == null){
            throw new NullPointerException("Il n'y a pas de données de livreurs");
        }

        config.setNbLivreurs(assignation.size());

        for(Map.Entry<String, List<String>> e : assignation.entrySet()){

            int idLivreur = Integer.parseInt(e.getKey());
            System.out.println(">>> CarteController: Livreur" + config.getNomPrenom(idLivreur));

            for(int i=0; i<e.getValue().size(); ++i){
                System.out.println(e.getValue() + " TTT " + i);

                Integer idLivraison = Integer.parseInt(e.getValue().get(i));
                System.out.println("Id : " + idLivraison);
                demandeLivraison.assignerLivreur(config.getLivreurbyId(idLivreur), idLivraison, carte);

                System.out.println(">>> CarteController: Trajets" + this.getCarte().getTrajets().getLast().getLivreur().getNom());
                System.out.println(">>> CarteController: Sites" + this.getCarte().getTrajets().getLast().getSites());
            }
        }
        System.out.println(">>> CarteController: Livreur" + config.getNbLivreur());
    }

  // --- modifications dans controller/CarteController.java ---

    public void calculerTournee() throws Exception {
        // Vérifications préalables
        if (carte == null) {
            throw new IllegalStateException("Carte non chargée");
        }
        if (carte.getSites() == null || carte.getSites().isEmpty()) {
            throw new IllegalStateException("Aucune demande / sites non chargés dans la carte");
        }
        if(config.getNbLivreur() == 0){
            throw new IllegalStateException("Aucun livreur assigné");
        }
        // Effacer les calculs précédents pour éviter d'utiliser des structures obsolètes
        this.effacerCalcul();

        // chercher entrepot
        Entrepot e = null;
        for (Site site : carte.getSites()) {
            if (site instanceof Entrepot) {
                e = (Entrepot) site;
                break;
            }
        }
        if (e == null) {
            throw new IllegalStateException("Entrepôt introuvable dans la carte");
        }

        // creer graphe total et calculer chemins minimaux
        // System.out.println("Création graphe total");
       creerGrapheTotal(carte, e.getId());
        if (gt == null ) {
            throw new IllegalStateException("La création du graphe a échoué");
        }

        for(Trajet trajet: this.getCarte().getTrajets()){
            //System.out.println("Trajet : "+ trajet);
            //System.out.println("Sites : "+ trajet.getSites());
            this.chercherCheminsMin(trajet.getSites(), trajet);
            //trajet.genererFeuilleDeRoute();
        }
       
        //this.supprimerLivraison(gt, Long.valueOf(25610684), Long.valueOf(21717915), this.getCarte().getTrajets().get(0));
        //this.supprimerLivraison(gt, Long.valueOf(21992645), Long.valueOf(55444215), this.getCarte().getTrajets().get(0));
        //this.supprimerLivraison(gt, Long.valueOf(55444018), Long.valueOf(26470086), this.getCarte().getTrajets().get(0));
        //this.supprimerLivraison(gt, Long.valueOf(27362899), Long.valueOf(505061101), this.getCarte().getTrajets().get(0));
        //TEST
        //ajouterLivraison( Long.valueOf(1679901320), Long.valueOf(342873658), Long.valueOf(26086123), Long.valueOf(208769039),carte.getTrajets().get(0));
        // supprimerLivraison();
        // undo();
        // redo();
        
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
        System.out.println(getCarteJSON());
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

        // -- Trajets 
        json.append(",\"trajets\":{");
        List<Trajet> trajets = carte.getTrajets();
        HashMap<Site,Long> sitesImpactes = new HashMap<>(); 
        List<LocalTime> heuresArrivees = new ArrayList<LocalTime>();;
        for (int i = 0; i < trajets.size(); i++) {
            Trajet t = trajets.get(i);
            if(t.getHeureFin() != null){
                heuresArrivees.add(t.getHeureFin());
            }
            json.append("\"").append(i).append("\":[");
            List<Troncon> troncons = t.getTroncons();
            for (int j = 0; j < troncons.size(); j++) {
                Troncon tr = troncons.get(j);
                json.append(String.format("{\"from\":%d,\"to\":%d}", 
                    tr.getOrigine().getId(), 
                    tr.getDestination().getId()));
                if (j < troncons.size() - 1) json.append(","); // <-- virgule entre tronçons
            }

            json.append("]");
            if (i < trajets.size() - 1) json.append(","); // <-- virgule entre trajets
            HashMap<Site,Long> sites = t.getSitesImpactes(); 
            if (!sites.isEmpty()) {
                for (var key : sites.keySet()) {
                    sitesImpactes.put(key, sites.get(key));
                }
            }
        }
        json.append("}");

        // -- Sites
        json.append(",\"sites\":[");
        boolean firstSite = true;
        for (Site s : carte.getSites()) {
            if (!firstSite) json.append(",");
            firstSite = false;
            Integer numLivraison = null;
            List<LocalTime> heuresArriveesTrajets = null;
            if (s instanceof Depot ) numLivraison = ((Depot) s).getNumLivraison();
            else if (s instanceof Collecte ) numLivraison = ((Collecte) s).getNumLivraison();
            else {
                ((Entrepot) s).changeHeures(heuresArrivees);
                heuresArriveesTrajets = ((Entrepot) s).getHeures();
            }
            try {
                double lat = s.getLatitude();
                double lng = s.getLongitude();
                json.append(String.format(Locale.US,
                    "{\"id\":%d,\"lat\":%f,\"lng\":%f,\"type\":\"%s\"",
                    s.getId(), lat, lng, s.getTypeSite()));

                if (s.getDepartHeure() != null) {
                    json.append(String.format(",\"depart\":\"%s\"", s.getDepartHeure().toString()));
                }
                if (heuresArriveesTrajets == null  && s.getArriveeHeure() != null) {
                    json.append(String.format(",\"arrivee\":\"%s\"", s.getArriveeHeure().toString()));
                }
                if (numLivraison != null) {
                    json.append(String.format(",\"numLivraison\":%d", numLivraison));
                }
                if (s.getNumPassage() != null) {
                    json.append(String.format(",\"numPassage\":%d", s.getNumPassage()));
                }
                if(!(heuresArriveesTrajets == null)){
                    json.append(",\"heures\": [");
                    for (int i = 0;i<heuresArriveesTrajets.size();++i){
                        json.append("\""+ heuresArriveesTrajets.get(i).toString() + "\"");
                        if(i<heuresArriveesTrajets.size()-1){
                            json.append(",");
                        }
                    }
                    json.append("]");
                }
                json.append("}");
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du site " + s.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        json.append("]");
        
        // -- Sites Impactes 
        json.append(",\"sitesImpactes\":[");
        firstSite = true;
        for (var s : sitesImpactes.keySet()) {
            if (!firstSite) json.append(",");
            firstSite = false;
            var delay = sitesImpactes.get(s);
            try {
                json.append(String.format(Locale.US,
                    "{\"id\":%d,\"delay\":\"%d\"}",
                    s.getId(), delay));
                
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du site " + s.getId() + ": " + e.getMessage());
                e.printStackTrace();
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

        GrapheTotal gt = new GrapheTotal(troncons, noeuds, idEntrepot);
        setGrapheTotal(gt);
        //gt.printGraphe();
        return gt;
    }

    public void chercherCheminsMin(List<Site> sites, Trajet trajet){
        // System.out.println("ChercherCheminMin (Controller)");
        gt.RechercheDijkstra(sites);
        // System.out.println("Fin recherche dijkstra");
        GrapheLivraison gl = new GrapheLivraison(sites.size(), gt.getMapDistances());
        gl.setContrainteHashMap(gt.getContrainteHashMap());

        TSP tsp = new TSP2();
        tsp.chercheSolution(60000, gl);

        List<Integer> solution = new ArrayList<Integer>();
        for (int i=0; i<gl.getNbSommets(); i++) {
            solution.add(gl.getIdFromIndex(tsp.getSolution(i)));
        }
        solution.add(solution.get(0));
        //System.out.println("Fin creation solution : " + solution);

        // Reconstruction du chemin complet : vérifier que getCheminComplet ne renvoie pas null
        List<Integer> cheminComplet = gt.getCheminComplet(solution);
        // System.out.println("Chemin Complet : " + cheminComplet);
        if (cheminComplet == null) {
            // Fournir un message utile pour le debug (indices, taille des maps, etc.)
            String msg = "Erreur: getCheminComplet a renvoyé null. Solution: " + solution
                         + " | mapCheminsMin keys: " + (gt.getCheminsMin() != null ? gt.getCheminsMin().keySet() : "null")
                         + " | mapDistances keys: " + (gt.getMapDistances() != null ? gt.getMapDistances().keySet() : "null");
            System.err.println(">>> chercherCheminsMin: " + msg);
            throw new IllegalStateException(msg);
        }

        List<Long> cheminCompletConverti = gt.convertirCheminComplet(cheminComplet);
        // System.out.println("cheminCompletConverti : " + cheminCompletConverti);
        CarteUtils.majTrajet(carte, gt, cheminCompletConverti, solution, trajet);
    }

    // Exécution d'une commande d'ajout
    public void ajouterLivraison(Long idCollecte,Long idPrecCollecte, Long idDepot,Long idPrecDepot,Trajet trajet) {
        Command alc = new AjouterLivraisonCommand(gt, idCollecte, idPrecCollecte, idDepot, idPrecDepot, trajet, carte);
        history.add(alc);
    }

    // Exécution d'une commande de suppression
    public void supprimerLivraison(Collecte collecte, Depot depot, Trajet trajet) {
        Command slc = new SupprimerLivraisonCommand(gt, collecte, depot, trajet, carte);
        history.add(slc);   
    }

    // Undo / Redo
    public void undo() { history.undo(); }

    public void redo() { history.redo(); }

    public void genererFeuillesdeRoute(){
        for(Trajet trajet: this.getCarte().getTrajets()){
            trajet.genererFeuilleDeRoute();
        }
    }

    public void resetCarte() {
        this.carte = new Carte();      // New empty map
        this.history.clear(); // Reset undo/redo
        this.config = new Configuration();   // Reset config if needed
        this.gt = null; // reset graphe ou calcul si existait

        System.out.println(">>> Carte réinitialisée.");
    }
}

