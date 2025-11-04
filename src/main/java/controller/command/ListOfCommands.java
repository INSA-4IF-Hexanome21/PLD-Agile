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
        l.add(++i,c);
        c.doCommand();
    }

    public void undo() {
        if (i >= 0) {
            l.get(i--).undoCommand();
        }
    }

    public void redo() {
        l.get(++i).doCommand();
    }
}
