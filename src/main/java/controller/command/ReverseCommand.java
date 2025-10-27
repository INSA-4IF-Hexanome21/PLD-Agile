package controller.command;

public class ReverseCommand implements Command {

    private Command command;

    public void ReverseCommand(Command cmd) {
        this.command = cmd;
    }

    public void doCommand() {
        command.undoCommand();
    }

    public void undoCommand() {
        command.doCommand();
    }
    
}

