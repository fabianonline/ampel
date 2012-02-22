package de.fabianonline.ampel.entities;

import java.util.ArrayList;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import de.fabianonline.ampel.Level;

public class Lane extends Entity {
	final static int WIDTH = 30;
	final static int LENGTH = 9999;
	
	int direction, number;
	int x, y;
	Transform transformation;
	Level level;
	Path path;
	int path_length;
	Vector<Double> path_part_lengths = new Vector<Double>();
	int[][] path_coordinates;
	public boolean can_take_cars;
	Car last_car = null;
	
	public Lane(Level level, int direction, int number) {
		can_take_cars = number!=3;
		this.level = level;
		this.direction = direction;
		this.number = number;
		
		x = level.half_width-1;
		y = (int) (level.half_height-1 + (number-2.5)*WIDTH);
		transformation = Transform.createRotateTransform((float) (direction*Math.PI/2.0), level.half_width, level.half_height);
		path = new Path(level.gamecontainer.getWidth(), (float) (y+0.5*WIDTH));
		path.lineTo((float) (x+2*WIDTH), (float)(y+0.5*WIDTH));
		switch(number) {
			case 0: 
				path.lineTo(365, 180);
				path.lineTo(357, 179);
				path.lineTo(352, 174);
				path.lineTo(351, 166);
				path.lineTo(351, -1000);
				break;
			case 1:
				path.lineTo(0, (float)(y+0.5*WIDTH));
				break;
			case 2:
				//path.lineTo(379, 239);
				path.lineTo(353, 248);
				path.lineTo(333, 262);
				path.lineTo(313, 277);
				path.lineTo(298, 294);
				path.lineTo(291, 308);
				path.lineTo(288, 326);
				path.lineTo(288, 1000);
				break;
			case 3:
				path = null;
				break;
		}
		if (path != null) {
			path = (Path) path.transform(transformation);
			path = cleanPath(path);
		}
		path_length = getPathLength();
		fillPathCoordinates();
	}
	
	private Path cleanPath(Path oldPath) {
		int width = level.gamecontainer.getWidth();
		int height = level.gamecontainer.getHeight();

		float[] pair = oldPath.getPoint(0);
		if (pair[0]<0) pair[0]=0;
		if (pair[1]<0) pair[1]=0;
		if (pair[0]>width) pair[0] = width;
		if (pair[1]>height) pair[1] = height;
		
		Path newPath = new Path(pair[0], pair[1]);
		for (int i=1; i<oldPath.getPointCount(); i++) {
			pair = oldPath.getPoint(i);
			if (pair[0]<0) pair[0]=0;
			if (pair[1]<0) pair[1]=0;
			if (pair[0]>width) pair[0] = width;
			if (pair[1]>height) pair[1] = height;
			newPath.lineTo(pair[0], pair[1]);
		}
		return newPath;
	}

	public int getPathLength() {
		if (path==null) return 0;
		float[] points = path.getPoints();
		float x, y, old_x = 0, old_y = 0;
		double length = 0;
		int i = 0;
		while (i < points.length) {
			x = points[i];
			y = points[i+1];
			if (x<0) x=0;
			if (x>level.gamecontainer.getWidth()) x=level.gamecontainer.getWidth();
			if (y<0) y=0;
			if (y>level.gamecontainer.getHeight()) y=level.gamecontainer.getHeight();
			i+= 2;
			
			if (old_x!=0 || old_y!=0) {
				length += Math.sqrt((x-old_x)*(x-old_x) + (y-old_y)*(y-old_y));
			}
			path_part_lengths.add(length);
			old_x = x;
			old_y = y;
		}
		return (int)length;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(new Color(0xFFAAAAAA));
		Shape lane = new Rectangle(x, y, LENGTH, WIDTH+2).transform(transformation); // Fahrspur
		g.fill(lane);
		g.setColor(new Color(0xffffffff));
		g.fill(new Rectangle((int) (x+2.5*WIDTH), y-1, LENGTH, 4).transform(transformation)); // Trennlinien
		if (number != 3) {
			g.fill(new Rectangle((int) (x+2.5*WIDTH), y-1, 4, WIDTH+1).transform(transformation));
		}
	}

	public int[] getCoords(int position) {
		return path_coordinates[position];
	}
	
	private void fillPathCoordinates() {
		if (path==null) return;
		path_coordinates = new int[path_length+1][];
		int current_path_index = 1;
		double current_path_length = path_part_lengths.get(1);
		double previous_path_length = path_part_lengths.get(0);
		float[] previous_point = path.getPoint(0);
		float[] current_point = path.getPoint(1);
		
		for (int position=0; position<=path_length; position++) {
			if (position > current_path_length) {
				current_path_index++;
				previous_path_length = current_path_length;
				current_path_length = path_part_lengths.get(current_path_index);
				previous_point = current_point;
				current_point = path.getPoint(current_path_index);
			}
			double remaining_length = position - previous_path_length;
			double length_factor = remaining_length / (current_path_length-previous_path_length);
			int target_x = (int)(previous_point[0] + (current_point[0]-previous_point[0])*length_factor);
			int target_y = (int)(previous_point[1] + (current_point[1]-previous_point[1])*length_factor);
			path_coordinates[position] = new int[] {target_x, target_y};
		}
		
	}

	public boolean has_place_for_new_car() {
		if (last_car==null) return true;
		return last_car.position > Car.WIDTH*2;
		/*if (last_car==null) return true;
		int[] start_coords = this.getCoords(1);
		return Math.sqrt((last_car.coords[0]-start_coords[0])*(last_car.coords[0]-start_coords[0]) + (last_car.coords[1]-start_coords[1])*(last_car.coords[1]-start_coords[1]))>Car.WIDTH*3;*/
	}
	
	public void add_car(Car car) {
		last_car = car;
	}
}
