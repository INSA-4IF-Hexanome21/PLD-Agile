package controller.command;

public interface Command {
    
    public void doCommand();
    public void undoCommand();
}
