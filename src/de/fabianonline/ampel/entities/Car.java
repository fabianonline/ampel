package de.fabianonline.ampel.entities;

import java.util.Random;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter.RandomValue;

import de.fabianonline.ampel.Ampel;

public class Car extends Entity {
	public static final int WIDTH = 16;
	public static final Vector<Car> cars = new Vector<Car>();
	private Lane lane;
	public double position = 0.0;
	private double speed = 0.1;
	private Color color;
	public boolean crashed = false;
	public int[] coords;
	
	public Car() throws Exception {
		throw new Exception("huh?");
	}

	public Car(Lane lane) {
		this.lane = lane;
		cars.add(this);
		color = new Color(0xff000000 | new Random().nextInt(0xffffff));
		coords = lane.getCoords((int) position);
	}
	
	public void update() {
		if (crashed) return;
		position += speed;
		coords = lane.getCoords((int) position);
		Car car = testForCrash();
		if (car != null) {
			this.crashed = true;
			car.crashed = true;
		}
	}
	
	public Car testForCrash() {
		for (Car car : cars) {
			if (car == this) continue;
			if (this.collides_with(car)) return car;
		}
		return null;
	}
	
	public void destroy() {
		super.destroy();
		cars.remove(this);
	}
	
	@Override
	public void draw(Graphics g) {
		if (lane==null) return;
		if (position>lane.path_length) {
			this.destroy();
			return;
		}
		g.setColor(color);
		g.fillOval(coords[0]-WIDTH/2, coords[1]-WIDTH/2, WIDTH, WIDTH);
	}
	
	public boolean collides_with(Car car) {
		return Math.sqrt((coords[0]-car.coords[0])*(coords[0]-car.coords[0]) + (coords[1]-car.coords[1])*(coords[1]-car.coords[1])) < WIDTH;
	}
}
