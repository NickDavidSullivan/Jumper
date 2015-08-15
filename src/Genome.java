import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Genome{
		private static Random random = new Random();
		
		private List<Double> weights;
		private double fitness;
		private double max_fitness;
		
		// Constructor, create empty.
		public Genome(){
			weights = new LinkedList<Double>();
			fitness = 0;
			max_fitness = 0;
		}
		
		// Constructor, randomly generate values.
		public Genome(int num_weights){
			weights = new LinkedList<Double>();
			fitness = 0;
			max_fitness = 0;
			for (int i=0; i<num_weights; i++){
				weights.add(new Double(random.nextDouble()*2 - 1));
			}
		}
		// Constructor, copy another genome.
		public Genome(Genome g){
			fitness = g.getFitness();
			max_fitness = g.getMaxFitness();
			weights = new LinkedList<Double>();
			ListIterator<Double> iter = g.getWeights().listIterator();
			while (iter.hasNext()){
				Double d = iter.next();
				Double d2 = new Double(d.doubleValue());
				weights.add(d2);
			}
		}
		
		// Constructor, use prefilled values.
		public Genome(List<Double> weights, double fitness){
			this.weights = weights;
			this.fitness = fitness;
		}
		
		public List<Double> getWeights(){
			return weights;
		}
		public double getFitness(){
			return fitness;
		}
		public double getMaxFitness(){
			return max_fitness;
		}
		public void setFitness(double fitness){
			this.fitness = fitness;
			if (fitness > max_fitness) max_fitness = fitness;
		}
		public void setMaxFitness(double max_fitness){
			this.max_fitness = max_fitness;
		}
		public void setWeights(List<Double> weights){
			this.weights = weights;
		}
		
		@Override
		public String toString(){
			String ret = "";
			ListIterator<Double> iter = weights.listIterator();
			while (iter.hasNext()){
				String str = String.format("%.03f, ", iter.next());
				ret += str;
			}
			return ret;
		}
	}