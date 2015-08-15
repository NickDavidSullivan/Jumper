import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math;

public class Main {
	private static final int TICKS_PER_RUN = 2000;
	private static final int NUM_GENERATIONS = 3;
	private static final int NUM_VEHICLES = 1000;
	private static final int NUM_OBSTACLES = 30;
	private static final int NUM_FIT = 20;
	
	public static void main(String[] args){
		// Create lists to hold the vehicles and checkpoints.
		List<Vehicle> vehicles = new LinkedList<Vehicle>();
		List<Obstacle> obstacles = new LinkedList<Obstacle>();
		Object mutex_lock = new Object();
		
		// Create and add neural networks and vehicles.
		//System.out.println("Hello.");
		for (int i=0; i<NUM_VEHICLES; i++){
			Vehicle vehicle = new Vehicle(100, 500);
			vehicles.add(vehicle);
		}
		
		// Obstacles.
		for (int i=0; i<NUM_OBSTACLES; i++){
			Obstacle obstacle = new Obstacle(1000, 480, 85*i);
			obstacles.add(obstacle);
		}
		
		// Create a genetic algorithm that stores the weights that are used in the neural networks.
		//System.out.println(new NeuralNet().getNumWeights());
		GeneAlgorithm geneAlgorithm = new GeneAlgorithm(vehicles, 0.2, 0.1, mutex_lock);
		
		// Create the environment to hold and update the vehicles and checkpoints.
		Environment environment = new Environment(vehicles, obstacles, mutex_lock);
		
		// Display the environment.
		Display display = new Display(environment, mutex_lock);
		
		// Repeat for generations.
		for (int gen=0; gen<NUM_GENERATIONS; gen++){
			double time = 0;
			System.out.println("Generation " + gen);
			try {
				Thread.sleep(1000);
			} catch (Exception e){};
			
			// Set the distances to checkpoints.
			environment.update(0.0);
			display.canvasRepaint();
			// Start the run
			for (int i=0; i<TICKS_PER_RUN; i++){
				// Run inputs through the neural network to get vehicle speeds.
				double dist = environment.getClosestNormalisedObstacleDist();
				ListIterator<Vehicle> iter = vehicles.listIterator();
				while (iter.hasNext()){
					iter.next().useNeuralNetwork(dist);
				}
				// Update the environment.
				environment.update(1);
				time++;
				display.canvasRepaint();
				try {
					Thread.sleep(10);
				} catch (Exception e){};
				if (environment.getNumAliveVehicles() <= 0){
					break;
				}
			}
			if (gen < NUM_GENERATIONS-1){
				// Move onto the next generation.
				vehicles = geneAlgorithm.nextGeneration(NUM_FIT, NUM_VEHICLES);
				environment.setVehicles(vehicles);
				display.canvasRepaint();
				
				// Move vehicles back to start point.
				environment.moveVehiclesToStart();
			}
		}
		
		// Final run.
		System.out.println("Result.");
		//Display display = new Display(environment, mutex_lock);
		
		vehicles = geneAlgorithm.nextGeneration(1, 1);
		environment.setVehicles(vehicles);
		Vehicle v = vehicles.get(0);
		Genome g = v.getGenome();
		double time = 0;
		double max_fitness = g.getMaxFitness();
		environment.moveVehiclesToStart();
		environment.update(0.0);
		display.canvasRepaint();
		while (g.getMaxFitness() < max_fitness){
			v.useNeuralNetwork(environment.getClosestNormalisedObstacleDist());
			environment.update(1);
			time++;
			display.canvasRepaint();
			try {
				Thread.sleep(10);
			} catch (Exception e){};
		}
		System.out.println(v.getNeuralNet());
	}
}