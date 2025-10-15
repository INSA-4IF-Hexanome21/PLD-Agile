package controller;

public interface State {
    /**
	 * Method called by the controller after a click on the button "ChargerCarte"
	 * @param c the controler
	 * @param w the window
	 */
	public default void chargerCarte(Controller c, Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Chargerlivraison"
	 * @param c the controler
	 * @param w the window
	 */
	public default void addRectangle(Controller c, Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Delete shapes"
	 * @param c the controler
	 * @param w the window
	 */
	public default void delete(Controller c, Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Move a shape"
	 * @param c the controler
	 * @param w the window
	 */
	public default void move(Controller c, Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Decrease scale"
	 * @param w the window
	 */
	public default void decreaseScale(Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Increase scale"
	 * @param w the window
	 */
	public default void increaseScale(Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Undo"
	 * @param l the current list of commands
	 */
	public default void undo(ListOfCommands l){};
	
	/**
	 * Method called by the controller after a click on the button "Redo"
	 * @param l the current list of commands
	 */
	public default void redo(ListOfCommands l){};
	
	/**
	 * Method called by the controller after a click on the button "Save the plan"
	 * @param p the plan
	 * @param w the window
	 */
	public default void save(Plan p, Window w){};
	
	/**
	 * Method called by the controller after a click on the button "Load a plan"
	 * @param p the plan
	 * @param l the current list of commands
	 * @param w the window
	 */
	public default void load(Plan p, ListOfCommands l, Window f){};

	/**
	 * Method called by the controller when the mouse is moved on the graphical view
	 * Precondition : p != null
	 * @param plan the plan
	 * @param p the point corresponding to the mouse position
	 */
	public default void mouseMoved(Plan plan, Point p){};
	
	/**
	 * Method called by the controller when a key is troken 
	 * @param p the plan
	 * @param l the current list of commands
	 * @param charCode the ASCII code of the corresponding character
	 */
	public default void keystroke(Plan p, ListOfCommands l, int charCode){};
	
	/**
	 * Method called by the controller after a right click
	 * @param c the controler
	 * @param w the window
	 * @param l the current list of commands
	 */
	public default void rightClick(Controller c, Window w, ListOfCommands l){
		w.allow(true);
		c.setCurrentState(c.initialState);
		w.displayMessage("");
	}
	
	/**
	 * Method called by the controller after a left click
	 * Precondition : p != null
	 * @param c the controler
	 * @param w the window
	 * @param plan the plan
	 * @param l the current list of commands
	 * @param p the coordinates of the mouse
	 */
	public default void leftClick(Controller c, Window w, Plan plan, ListOfCommands l, Point p){};
}
