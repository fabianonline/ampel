package de.fabianonline.ampel.entities;

import org.newdawn.slick.Graphics;

import de.fabianonline.ampel.Ampel;

public abstract class Entity {
	public Entity() {
		Ampel.entities.add(this);
	}
	
	abstract public void draw(Graphics g);
	
	public void update() {}
	
	public void destroy() {
		Ampel.deletableEntities.add(this);
	}
}
