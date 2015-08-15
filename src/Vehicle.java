import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.LinkedList;
public class Vehicle{
	public double pos_x, pos_y;
	public double vel_x, vel_y;
	public boolean wants_to_jump;
	public boolean dead;
	
	private NeuralNet neural_network;
	private Genome genome;
	private double width = 15;				//Pixels
	private double height = 10;
	
	// Constructor. Sets vars, creates a neural network, creates a genome based on that network,
	// then updates the network to have the same values as the genome.
	public Vehicle(double pos_x, double pos_y){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		wants_to_jump = false;
		dead = false;
		neural_network = new NeuralNet();
		genome = new Genome(neural_network.getNumWeights());
		neural_network.setWeights(genome.getWeights());
	}
	
	// Constructor. Copy another vehicle.
	public Vehicle(Vehicle v){
		this.pos_x = v.pos_x;
		this.pos_y = v.pos_y;
		this.wants_to_jump = v.wants_to_jump;
		this.dead = dead;
		this.neural_network = new NeuralNet();
		this.genome = new Genome(v.getGenome());
		this.neural_network.setWeights(genome.getWeights());
		
	}
	// Updates 'wants_to_jump' based on the neural network output. Input should be between -1 and 1.
	public void useNeuralNetwork(double nearest_block){
		LinkedList<Double> inputs = new LinkedList<Double>();
		inputs.add(nearest_block);
		
		List<Double> outputs = neural_network.update(inputs);
		if ( outputs.get(0).doubleValue() >= 0.5){
			wants_to_jump = true;
		}
	}
	
	// Returns a shape, ready for displaying.
	public Shape getShape(){
		Rectangle2D.Double rectangle = new Rectangle2D.Double(pos_x, pos_y, width, height);
		return rectangle;
	}
	
	// Returns the internal color of the shape.
	public Color getColor(){
		Color col = Color.GRAY;
		if (dead) col = Color.RED;
		return col;
	}
	
	public NeuralNet getNeuralNet(){
		return neural_network;
	}
	
	public Genome getGenome(){
		return genome;
	}
}