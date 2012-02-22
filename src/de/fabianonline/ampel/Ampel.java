package de.fabianonline.ampel;

import java.util.Random;
import java.util.Vector;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import de.fabianonline.ampel.entities.Entity;

public class Ampel extends BasicGame {
	public static Vector<Entity> entities = new Vector<Entity>();
	public static Vector<Entity> deletableEntities = new Vector<Entity>();
	Level level;
	static Random random;
	
	
	public Ampel() {
		super("Ampel");
		random = new Random();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		for (Entity entity: entities) {
			entity.draw(g);
		}
		clearEntities();
	}

	private void clearEntities() {
		for (Entity entity: deletableEntities) {
			entities.remove(entity);
		}
		deletableEntities.removeAllElements();
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		level = new Level(container);
		//container.setMaximumLogicUpdateInterval(50);
		//container.setMinimumLogicUpdateInterval(20);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();
		if (input.isMousePressed(0)) {
			System.out.println(input.getMouseX() + " : " + input.getMouseY());
		}
		level.update();
		for (Entity entity : entities) {
			entity.update();
		}
		clearEntities();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Ampel());
			app.start();
		} catch (SlickException ex) {
			ex.printStackTrace();
		}
	}

}
