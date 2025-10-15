package controller;

public class Controller{
	private State currentState;

    // Instances associated with each possible state of the controller
	protected final InitialState initialState = new InitialState();
	protected final CarteChargeState carteChargeState = new CarteChargeState();
	protected final LivraisonChargeState livraisonChargeState = new LivraisonChargeState();
	protected final LivraisonCalculeState livraisonCalculeState = new LivraisonCalculeState();
	protected final DeleteState deleteState = new DeleteState();

    /**
	 * Create the controller of the application
	 * @param plan the plan
	 * @param scale the graphical view scale 
	 */
	public Controller(Plan plan, int scale) {
		this.plan = plan;
		listOfCommands = new ListOfCommands();
		currentState = initialState;
		window = new Window(plan, scale, this);
	}

    /**
	 * Change the current state of the controller
	 * @param state the new current state
	 */
	protected void setCurrentState(State state){
		currentState = state;
	}

	// Methods corresponding to user events 
	/**
	 * Method called by window after a click on the button "Add a circle"
	 */
	public void addCircle() {
		currentState.addCircle(this, window);
	}

	/**
	 * Method called by window after a click on the button "Add a rectangle"
	 */
	public void addRectangle() {
		currentState.addRectangle(this, window);
	}

	/**
	 * Method called by window after a click on the button "Delete shapes"
	 */
	public void delete() {
		currentState.delete(this, window);
	}

	/**
	 * Method called by window after a click on the button "Move a shape"
	 */
	public void move() {
		currentState.move(this, window);
	}
	
	/**
	 * Method called by window after a click on the button "Decrease scale"
	 */
	public void decreaseScale(){
		currentState.decreaseScale( window);
	}

	/**
	 * Method called by window after a click on the button "Increase scale"
	 */
	public void increaseScale(){
		currentState.increaseScale(window);
	}
	
	/**
	 * Method called by window after a click on the button "Undo"
	 */
	public void undo(){
		currentState.undo(listOfCommands);
	}

	/**
	 * Method called by window after a click on the button "Redo"
	 */
	public void redo(){
		currentState.redo(listOfCommands);
	}
	
	/**
	 * Method called by window after a click on the button "Save the plan"
	 */
	public void save() {
		currentState.save(plan, window);
	}

	/**
	 * Method called by window after a click on the button "Load a plan"
	 */
	public void load() {
		currentState.load(plan, listOfCommands, window);
	}

	/**
	 * Method called by window after a left click on a point of the graphical view 
	 * Precondition : p != null
	 * @param p = coordinates of the click in the plan 
	 */
	public void leftClick(Point p) {
		currentState.leftClick(this, window,plan,listOfCommands,p);
	}
	/**
	 * Method called by window after a right click
	 */
	public void rightClick(){
		currentState.rightClick(this, window, listOfCommands);
	}

	/**
	 * Method called by window when the user moves the mouse on the graphical view
	 * Precondition : p != null
	 * @param p = coordinates of the mouse in the plan
	 */
	public void mouseMoved(Point p) {
		currentState.mouseMoved(plan, p);
	}

	/**
	 * Method called by window after a keystroke
	 * @param charCode the ASCII code of the corresponding char
	 */
	public void keystroke(int charCode) {
		currentState.keystroke(plan, listOfCommands, charCode);
	}
}
