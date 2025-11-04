package model;

import java.util.*;

public class Configuration {

    // Attributs
    private List<String> prenoms = Arrays.asList(
        // Prénoms masculins classiques
        "Pierre", "Jean", "Michel", "Philippe", "Jacques",
        
        // Prénoms masculins récents
        "Gabriel", "Léo", "Arthur", "Louis", "Lucas",
        
        // Prénoms féminins classiques
        "Marie", "Catherine", "Isabelle", "Sophie", "Nathalie",
        
        // Prénoms féminins récents
        "Jade", "Louise", "Emma", "Alice", "Chloé",

        // Prénoms de l'hexanôme
        "Peter","Lucie","Nathan","Asa","Ewan","Elie",

        // Prénoms pour jeu de mot
        "Liv","Lou","Fred","Colin","Zone"
    );

    private List<String> noms = Arrays.asList(
        // Noms très courants
        "Martin", "Bernard", "Dubois", "Thomas", "Robert",
        
        // Noms de métiers
        "Lefebvre", "Lemaire", "Mercier", "Boucher", "Fournier",
        
        // Noms géographiques
        "Dupont", "Dufour", "Durand", "Fontaine", "Blanc",
        
        // Noms régionaux
        "Moreau", "Laurent", "Simon", "Michel", "Garcia",

        // Noms de l'hexanôme
        "Yaacoub","Lataste","Aknin","Diaz","Garoux","Ravoux",

        // Noms pour jeu de mot
        "Reur","Garrou","Ex","Simo","Asma"
    );


    
    private Integer nbLivreurs;
    private List<Livreur> livreurs;

    // Constructeur
    public Configuration() {
        this.nbLivreurs = 0;
        this.livreurs = new ArrayList<Livreur>();
    }

    // Getters et Setters
    public boolean setNbLivreurs(Integer nbLiv){
        if(nbLiv == 0){
            return false;
        }
        
        if(this.nbLivreurs > nbLiv){
            for(int i = livreurs.size() - 1;i>=nbLiv;--i){
                livreurs.remove(i);
            }
            return true;
        }
        else{
            for(int i = this.nbLivreurs - 1;i<nbLiv;++i){
                Livreur livreur = obtenirNouveauLivreur(i);
                livreurs.add(livreur);
            }
            return true;
        }
    }

    public Integer getNbLivreur(){
        return this.nbLivreurs;
    }

    public Livreur getLivreurbyId(Integer id){
        return this.livreurs.get(id);
    }
    

    // Méthodes 
    private Livreur obtenirNouveauLivreur(Integer id){
        Random random = new Random();
        String prenom = this.prenoms.get(random.nextInt(prenoms.size()));
        String nom = this.noms.get(random.nextInt(prenoms.size()));
        return new Livreur(id,nom,prenom);
    }
    
}
