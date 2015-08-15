import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.LinkedList;
public class Obstacle{
	public double pos_x, pos_y;
	public double time_to_spawn;
	public double index;
	
	private double width = 30;				//Pixels
	private double height = 30;
	
	// Constructor. Sets vars, creates a neural network, creates a genome based on that network,
	// then updates the network to have the same values as the genome.
	public Obstacle(double pos_x, double pos_y, double time_to_spawn){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.time_to_spawn = time_to_spawn;
	}

	// Returns a shape, ready for displaying.
	public Shape getShape(){
		Rectangle2D.Double rectangle = new Rectangle2D.Double(pos_x, pos_y, width, height);
		return rectangle;
	}
	
	// Returns the internal color of the shape.
	public Color getColor(){
		Color col = Color.GREEN;
		return col;
	}
	
}