package controller.command;

import java.util.LinkedList;

public class ListOfCommands {

    private LinkedList<Command> l;
    private int i; 

    public ListOfCommands() {
        i = -1;
        l = new LinkedList<Command>();
    }

    public void add(Command c) {
        // Supprimer toutes les commandes après i
        while (l.size() > i + 1) {
            l.removeLast();
        }
        i++;
        l.add(c);
        c.doCommand();
    }

    synchronized public void undo() {
        try {
            if (i >= 0) {
                System.out.println("Annulation de la commande à l'index " + i);
                l.get(i).undoCommand();
                i--;
                System.out.println("Undo réussi, nouvel index : " + i);
            } else {
                System.out.println("Rien à annuler ! (i=" + i + ", size=" + l.size() + ")");
            }
        } catch (Exception e) {
            System.err.println("!!! ERREUR dans undo() !!!");
            e.printStackTrace();
            // Ne pas laisser l'exception remonter
        }
    }

    synchronized public void redo() {
        if (i + 1 < l.size()) {  // Vérifier qu'il y a un élément à refaire
            i++;
            l.get(i).doCommand();
        } else {
            System.out.println("Rien à refaire !");
        }
    }

    public void clear() {
        l.clear();
    }
}
