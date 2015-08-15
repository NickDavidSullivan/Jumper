import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;


public class GeneAlgorithm {
	private static int NUM_FITTEST = 20;
	private static Random random = new Random();
	
	private List<Vehicle> vehicles;
	private Object mutex_lock;
	
	private int weights_per_genome;
	private double total_fitness;
	private double best_fitness;
	private double average_fitness;
	private double worst_fitness;
	private int fittest_genome_index;
	private double mutation_rate;
	private double crossover_rate;
	private double max_perturbation;
	private int generation;
	
	// Create a new set of genomes with randomly filled weights.
	public GeneAlgorithm(List<Vehicle> vehicles, double mutation_rate, double crossover_rate, Object mutex_lock){
		this.vehicles = vehicles;
		total_fitness = 0;
		best_fitness = 0;
		average_fitness = 0;
		worst_fitness = 0;
		fittest_genome_index = 0;
		max_perturbation = 0.1;
		this.mutation_rate = mutation_rate;
		this.crossover_rate = crossover_rate;
		this.mutex_lock = mutex_lock;
	}
	
	// Alters weights randomly within the specified amount.
	public List<Double> mutate(List<Double> weights){
		List<Double> mutated_weights = new LinkedList<Double>();
		for (int i=0; i<weights.size(); i++){
			Double val = weights.get(i);
			if (random.nextDouble() < mutation_rate){
				mutated_weights.add(new Double(val.doubleValue() +
									max_perturbation * (random.nextDouble()*2.0 -1.0) ));
			} else {
				mutated_weights.add(new Double(val.doubleValue()));
			}
		}
		return mutated_weights;
	}
	
	// Sets the genomes as the weights of the neural network.
	public void applyGenomes(){
		int count = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			v.getNeuralNet().setWeights(v.getGenome().getWeights());
		}
		
	}
	
	// Chooses the best vehicles, then creates a new generation from them.
	public List<Vehicle> nextGeneration(int num_fittest, int population){
		// Sort the vehicle list based on best fitness.
		Collections.sort(vehicles, new VehicleComparator());
		//System.out.println(vehicles.get(vehicles.size()-1).getGenome().getMaxFitness());
		
		synchronized(mutex_lock){
			// Delete all vehicles that aren't the best.
			int size = vehicles.size();
			for (int i=0; i<size-num_fittest; i++){
				vehicles.remove(0);
			}
			//System.out.println(vehicles.size());
			// Repopulate the list with mutations of the fittest.
			while (vehicles.size() < population){
				for (int i=num_fittest-1; i>=0; i--){
					Vehicle v = vehicles.get(i);
					Vehicle v2 = new Vehicle(v);
					v2.getGenome().setWeights(mutate(v2.getGenome().getWeights()));
					v2.getNeuralNet().setWeights(v2.getGenome().getWeights());
					vehicles.add(v2);
					if (vehicles.size() >= population-1) break;
				}
			}
		}
		return vehicles;
	}
	
	
	/**************************************************************************************
	 * Inner class, VehicleComparator. Used in sorting vehicles by genome fitness.
	 *************************************************************************************/
	private class VehicleComparator implements Comparator<Vehicle> {
		
		public VehicleComparator(){}
		
		@Override
		public int compare(Vehicle v1, Vehicle v2){
			double f1 = v1.getGenome().getMaxFitness();
			double f2 = v2.getGenome().getMaxFitness();
			if (f1 < f2) return -1;
			if (f1 > f2) return 1;
			return 0;
		}
	}
	 
}