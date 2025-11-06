package model;

import java.util.*;

import model.utils.CarteUtils;

public class Carte {

    // Attributs
    private HashMap<Long, Noeud> noeuds;
    private List<Troncon> troncons;
    private List<Site> sites;
    private List<Trajet> trajets;
    private Integer nbLivraisons;

    // Constructeur
    public Carte() {
        this.noeuds = new HashMap<>();
        this.troncons = new ArrayList<>();
        this.sites = new ArrayList<>();
        this.trajets = new ArrayList<>();
        this.nbLivraisons = 0;
    }

    // Getters et Setters
    public HashMap<Long, Noeud> getNoeuds() {
        return noeuds;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public List<Site> getSites() {
        return sites;
    }

    public List<Trajet> getTrajets() {
        return trajets;
    }

    public void setNoeuds(HashMap<Long, Noeud> noeuds) {
        this.noeuds = noeuds;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }

    public void setNbLivraisons(Integer nbLivraisons){
        this.nbLivraisons = nbLivraisons;
    }

    public Integer getNbLivraisons(){
        return this.nbLivraisons;
    }

    // Méthodes 
    public void ajouterNoeud(Noeud noeud) {
        if (!noeuds.containsKey(noeud.getId())) this.noeuds.put(noeud.getId(), noeud);
    }

    public void ajouterTroncon(Troncon troncon) {
        if (!troncons.contains(troncon)) this.troncons.add(troncon);
    }

    public void ajouterSite(Site site) {
        this.sites.add(site);
    }

    public void supprimerSite(Site site) {
        this.sites.remove(site);
    }

    public void ajouterTrajet(Trajet trajet) {
        this.trajets.add(trajet);
    }

    public void supprimerTrajet(Trajet trajet) {
        this.trajets.remove(trajet);
    }

    public void ajouterLivraison(GrapheTotal gt, long idCollecte, long idPrecCollecte, Integer dureeEnlevement, long idDepot, long idPrecDepot, Integer dureeLivraison, Trajet trajet, Carte carte) {
        //Creation des nouveaux sites
        Noeud noeudCollecte = gt.trouverNoeud(idCollecte);
        Collecte collecte = new Collecte(idCollecte, noeudCollecte.getLatitude(), noeudCollecte.getLongitude(), carte.getNbLivraisons()+1, dureeEnlevement);
        Noeud noeudDepot = gt.trouverNoeud(idDepot);
        Depot depot = new Depot(idDepot, noeudDepot.getLatitude(), noeudDepot.getLongitude(), carte.getNbLivraisons()+1, dureeLivraison);
        trajet.addSite(collecte);
        trajet.addSite(depot);
        carte.ajouterSite(collecte);
        carte.ajouterSite(depot);

        List<Long> solutionLong = trajet.getSolution();
        List<Long> nouvSolutionLong = new ArrayList<>();
        List<Long> nouvCheminComplet = new ArrayList<>();

        Integer indexCollecteSol = 0;
        Integer indexDepotSol = 0;

        for (int i = 0; i<solutionLong.size();++i){
            Long idSite = solutionLong.get(i);
            nouvSolutionLong.add(idSite);
            if(i<solutionLong.size() - 1 && Objects.equals(idSite, idPrecCollecte)){
                nouvSolutionLong.add(idCollecte);
                indexCollecteSol = i+1;
            }
            else if(i<solutionLong.size() - 1 && Objects.equals(idSite, idPrecDepot)){
                nouvSolutionLong.add(idDepot);
                indexDepotSol = i+2;
            }
        }


        //Recherche Dijkstra Collecte
        List<Site> siteARechercher = new ArrayList<Site>();
        siteARechercher.add(trajet.getSite(idCollecte));
        siteARechercher.add(trajet.getSite(idPrecCollecte)); // Ajout du Site précédent
        siteARechercher.add(trajet.getSite(nouvSolutionLong.get(indexCollecteSol + 1))); // Ajout du Site suivant

        siteARechercher.add(trajet.getSite(idDepot));
        siteARechercher.add(trajet.getSite(idPrecDepot)); // Ajout du Site précédent
        siteARechercher.add(trajet.getSite(nouvSolutionLong.get(indexDepotSol + 1))); // Ajout du Site suivant

        gt.RechercheDijkstra(siteARechercher);

        //Recréation du chemin
        for (var i = 0; i<nouvSolutionLong.size()-1;++i) {
            List<Long> chemin = CarteUtils.getChemin(gt, nouvSolutionLong.get(i), nouvSolutionLong.get(i+1));
            CarteUtils.ajoutSansDuplication(nouvCheminComplet,chemin);
        }

        // conversion de la solution pour passage a majTrajet
        List<Integer> solution = new ArrayList<>();
        for (var id : nouvSolutionLong) {
            solution.add(gt.getIndexFromId(id));
        }
        // System.out.println("Nouvelle solution : " + solution);
        CarteUtils.majTrajet(carte, gt, nouvCheminComplet, solution, trajet);
        // System.out.println(trajet.sitesImpactes);

    }

    public void supprimerLivraison(GrapheTotal gt, Long idCollecte, Long idDepot, Trajet trajet, Carte carte) {
        List<Site> sites = trajet.getSites();
        List<Site> sitesToRemove = new ArrayList<>();
        List<Long> solutionLong = trajet.getSolution();
        List<Long> nouvSolutionLong = new ArrayList<>();
        List<Long> nouvCheminComplet = new ArrayList<>();

        // on crée la nouvelle solution en retirant les sites à supprimer
        for (var s : solutionLong ) {
            if (!Objects.equals(s, idCollecte) && !Objects.equals(s, idDepot)) nouvSolutionLong.add(s);
        } 

        // on supprime les sites de trajets 
        for (var s : sites) {
            if (Objects.equals(s.getId(), idCollecte) || Objects.equals(s.getId(), idDepot)) {
                sitesToRemove.add(s);
                carte.supprimerSite(s);
            }
        }
        for (var s : sitesToRemove) trajet.removeSite(s);

        for (var i = 0; i<nouvSolutionLong.size()-1;++i) {
            List<Long> chemin = CarteUtils.getChemin(gt, nouvSolutionLong.get(i), nouvSolutionLong.get(i+1));
            CarteUtils.ajoutSansDuplication(nouvCheminComplet,chemin);
        }
        
        // conversion de la solution pour passage a majTrajet
        List<Integer> solution = new ArrayList<>();
        for (var id : nouvSolutionLong) {
            solution.add(gt.getIndexFromId(id));
        }
        CarteUtils.majTrajet(carte, gt, nouvCheminComplet, solution, trajet);
    }

    public Site getSiteById(Long id) {
        for (Site s : sites) {
            if (Objects.equals(s.getId(),id)) return s;
        }
        return null;
    }

    public Trajet getTrajetParLivreur(Long idLivreur){
        Trajet trajetLivreur = null;
        for(Trajet trajet: this.getTrajets()){
            if(trajet.getLivreur().getId() == idLivreur){
                trajetLivreur = trajet;
                break;
            }
        }
        return trajetLivreur;
    }

    public void resetTournee(){
        this.trajets.clear();
        this.sites.clear();
        System.out.println("Carte reset");
    }

    public Long getSiteAssocie(Integer numLivraison,Long idSite){
        Site siteInit = getSiteById(idSite);
        String typeRecherche = "";
        if(siteInit instanceof Collecte){
            typeRecherche = "depot";
        }
        else if(siteInit instanceof Depot){
            typeRecherche = "collecte";
        }
        for(Site site : this.sites){
            if(typeRecherche == "depot" && site instanceof Depot && site.getId() != idSite && ((Depot)site).getNumLivraison() == numLivraison){
                return site.getId();
            }
            else if(typeRecherche == "collecte" && site instanceof Collecte && site.getId() != idSite && ((Collecte)site).getNumLivraison() == numLivraison){
                return site.getId();
            }
        }
        return null;
    }
}
