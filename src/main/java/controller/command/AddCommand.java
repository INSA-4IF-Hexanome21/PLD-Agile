package controller.command;

import model.Carte;
import model.Site;

public class AddCommand implements Command {
    
    private Carte carte;
    private Site site;

    public void AddCommand(Carte c, Site s) {
        this.carte = c;
        this.site = s;
    }

    public void doCommand() {
        // TODO: to implement
    }

    public void undoCommand() {
        // TODO: to implement
    }
    
}
