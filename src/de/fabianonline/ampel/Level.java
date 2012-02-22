package de.fabianonline.ampel;

import java.util.Random;
import java.util.Vector;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;

import de.fabianonline.ampel.entities.Car;
import de.fabianonline.ampel.entities.Lane;

public class Level {
	Vector<Lane> lanes = new Vector<Lane>();
	Vector<Lane> lanes_accepting_cars = new Vector<Lane>();
	Vector<Car> cars = new Vector<Car>();
	public GameContainer gamecontainer;
	public int half_height, half_width;
	
	public Level(GameContainer gamecontainer) {
		half_height = gamecontainer.getHeight() / 2;
		half_width = gamecontainer.getWidth() / 2;
		
		this.gamecontainer = gamecontainer;
		
		Lane lane;
		for (int dir=0; dir<4; dir++) {
			for (int number=0; number<4; number++) {
				lane = new Lane(this, dir, number);
				lanes.add(lane);
				if(lane.can_take_cars) lanes_accepting_cars.add(lane);
			}
		}
		
		for (int i=0; i<lanes_accepting_cars.size(); i++) {
			try {
				new Car(lanes_accepting_cars.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
		if (Ampel.random.nextFloat()<0.01) {
			Lane lane = lanes_accepting_cars.get(Ampel.random.nextInt(lanes_accepting_cars.size()));
			if (lane.has_place_for_new_car()) {
				lane.add_car(new Car(lane));
			}
		}
	}
}
