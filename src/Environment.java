import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math;
import java.awt.geom.Area;

public class Environment {
	private static final double COLLISION_DIST = 10;
	private static final double LINEAR_SPEED_MULT = 2;
	private static final double ANGULAR_SPEED_MULT = 0.08;
	
	private int obs_index;
	private List<Vehicle> vehicles;
	private List<Obstacle> obstacles;
	private List<Obstacle> active_obstacles;
	private List<Obstacle> dead_obstacles;
	private Object mutex_lock;
	
	public double time;
	
	public Environment(List<Vehicle> vehicles, List<Obstacle> obstacles, Object mutex_lock){
		this.vehicles = vehicles;
		this.obstacles = obstacles;
		this.mutex_lock = mutex_lock;
		active_obstacles = new LinkedList<Obstacle>();
		dead_obstacles = new LinkedList<Obstacle>();
		time = 0;
		obs_index = 0;
	}
	
	// Move vehicles to start point, and reset the checkpoint index.
	public void moveVehiclesToStart(){
		time = 0;
		obs_index = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			v.pos_x = 100;
			v.pos_y = 500;
			v.vel_x = 0;
			v.vel_y = 0;
			v.wants_to_jump = false;
			v.dead = false;
			v.getGenome().setFitness(0);
			v.getGenome().setMaxFitness(0);
		}
		// Add active and dead back into obstacles.
		ListIterator<Obstacle> obs_iter = active_obstacles.listIterator();
		while (obs_iter.hasNext()){
			Obstacle o = obs_iter.next();
			obstacles.add(o);
		}
		active_obstacles = new LinkedList<Obstacle>();
		obs_iter = dead_obstacles.listIterator();
		while (obs_iter.hasNext()){
			Obstacle o = obs_iter.next();
			obstacles.add(o);
		}
		dead_obstacles = new LinkedList<Obstacle>();
		// Move obstacles back to start.
		obs_iter = obstacles.listIterator();
		while (obs_iter.hasNext()){
			Obstacle o = obs_iter.next();
			o.pos_x = 1000;
		}
	}
	
	// Assuming all vehicle speeds have been updated, this moves them and detects if the checkpoint
	// has been reached.
	public void update(double time_increment){
		time += time_increment;
		// Spawn new obstacles.
		ListIterator<Obstacle> obs_iter = obstacles.listIterator();
		while (obs_iter.hasNext()){
			Obstacle o = obs_iter.next();
			if (o.time_to_spawn < time && o.time_to_spawn >= 0){
				o.index = obs_index++;
				active_obstacles.add(o);
				obs_iter.remove();
			}
		}
		// Update active obstacles.
		obs_iter = active_obstacles.listIterator();
		while (obs_iter.hasNext()){
			Obstacle o = obs_iter.next();
			o.pos_x-= 5;
			if (o.pos_x <= 10){
				dead_obstacles.add(o);
				obs_iter.remove();
			}
		}
		
		// Update vehicles.
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			// Move each vehicle.
			v.vel_y += 0.2;
			v.pos_y += v.vel_y * time_increment;
			if ( v.pos_y > 500 ) v.pos_y = 500;
			if (v.wants_to_jump && v.pos_y == 500) v.vel_y = -8;
			v.wants_to_jump = false;
			
			// Check collision.
			obs_iter = active_obstacles.listIterator();
			while (obs_iter.hasNext()){
				Obstacle o = obs_iter.next();
				Area a1 = new Area(v.getShape());
				Area a2 = new Area(o.getShape());
				if (a1.intersects( a2.getBounds2D() )){
					v.dead = true;
				}
			}
			// Update fitness.
			if (!v.dead){
				v.getGenome().setFitness(v.getGenome().getFitness() + 1);
			}
		}
	}
	
	// Returns the closest obstacle distance between 0 and 1.
	public double getClosestNormalisedObstacleDist(){
		double dist = 1.0;
		ListIterator<Obstacle> iter = active_obstacles.listIterator();
		while (iter.hasNext()){
			Obstacle o = iter.next();
			if (dist > o.pos_x / 1000.0){
				dist = o.pos_x / 1000.0;
			}
		}
		return dist;
	}
	public int getNumAliveVehicles(){
		int count = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			if (!v.dead) count++;
		}
		return count;
	}
	public int getNumVehicles(){
		return vehicles.size();
	}
	
	public int getNumActiveObstacles(){
		return active_obstacles.size();
	}
	
	public Vehicle getVehicle(int index){
		return vehicles.get(index);
	}	
	
	public Obstacle getActiveObstacle(int index){
		return active_obstacles.get(index);
	}
	
	public void setVehicles(List<Vehicle> vehicles){
		synchronized(mutex_lock){
			this.vehicles = vehicles;
		}
	}
}