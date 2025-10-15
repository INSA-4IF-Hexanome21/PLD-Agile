package controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import model.Plan;
import view.Window;
import xml.XMLdeserializer;
import xml.ExceptionXML;
import xml.XMLserializer;

public class InitialState implements State {
// Etat initial 
	@Override
	public void addCircle(Controller c, Window window) {
		window.allow(false);
		window.displayMessage("Add a circle: [Left Click] on the center of the new circle; " +
				"[Right Click] to cancel");
		c.setCurrentState(c.circleState1);
	}

	@Override
	public void addRectangle(Controller c, Window window) {
		window.allow(false);
		window.displayMessage("Add a rectangle: [Left Click] on a corner of the new rectangle; " +
				"[Right Click] to cancel");
		c.setCurrentState(c.rectangleState1);
	}

	@Override
	public void delete(Controller c, Window window){
		window.allow(false);
		window.displayMessage("Deletion: [Left Click] on a shape to delete; " +
				"[Right Click] to exit deletion mode");
		c.setCurrentState(c.deleteState);
	}

	@Override
	public void move(Controller c, Window window){
		window.allow(false);
		window.displayMessage("Move a shape: [Left Click] to select a shape to move; " +
				"[Right Click] to cancel");
		c.setCurrentState(c.moveState1);
	}
	
	@Override
	public void decreaseScale(Window window) {
		int scale = window.getScale();
		if (scale > 1){
			window.setScale(scale-1);
			window.displayMessage("New scale = "+(scale-1));
		}
		else window.displayMessage("Scale cannot be decreased");
	}
	
	@Override
	public void increaseScale(Window window) {
		int scale = window.getScale();
		if (scale < 15){
			window.setScale(scale+1);		
			window.displayMessage("New scale = "+(scale+1));
		}
		else window.displayMessage("Scale cannot be increased");
	}

	@Override
	public void undo(ListOfCommands listOfCdes){
		listOfCdes.undo();
	}

	@Override
	public void redo(ListOfCommands listOfCdes){
		listOfCdes.redo();
	}

	@Override
	public void save(Plan plan, Window window){
		try {
			XMLserializer.getInstance().save(plan);
		} catch (ParserConfigurationException
				| TransformerFactoryConfigurationError
				| TransformerException | ExceptionXML e) {
			window.displayMessage(e.getMessage());
		}
	}


	@Override
	public void load(Plan plan, ListOfCommands listOfCdes, Window window){
		int width = plan.getWidth();
		int height = plan.getHeight();
		try {
			XMLdeserializer.load(plan);
		} catch (ParserConfigurationException 
				| SAXException | IOException 
				| ExceptionXML | NumberFormatException e) {
			window.displayMessage(e.getMessage());
			plan.reset(width, height);
		}
		listOfCdes.reset();
	}


}
