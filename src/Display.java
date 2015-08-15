import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Display {
	private JFrame mainFrame;   // Contains backPanel
    private JPanel backPanel;   // Contains all other panels
	
	public Display(Environment env, Object mutex_lock){
		// Create the main frame
        mainFrame = new JFrame("Neural Network Test");
        mainFrame.setSize(950,800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create the background
        backPanel = new Canvas(env, mutex_lock);
        backPanel.setBackground(Color.WHITE);
		
		mainFrame.add(backPanel);
        mainFrame.setVisible(true);
	}
	
	public void canvasRepaint(){
		backPanel.repaint();
	}
	/**************************************************************************************
	 * Inner class, Canvas. Draws the vehicle and checkpoints.
	 *************************************************************************************/
	private class Canvas extends JPanel {
		private Environment environment;
		private Object mutex_lock;
		
		public Canvas(Environment env, Object mutex_lock){
			super();
			this.environment = env;
			this.mutex_lock = mutex_lock;
		}
		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			// Draw all vehicles.
			synchronized(mutex_lock){
				for (int i=0; i<environment.getNumVehicles(); i++){
					Vehicle vehicle = environment.getVehicle(i);
					if (!vehicle.dead){
						Shape shape = vehicle.getShape();
						Color col = vehicle.getColor();
						g2d.setColor(col);
						g2d.fill(shape);
						g2d.setColor(Color.BLACK);
						g2d.draw(shape);
					}
				}
				
				// Draw obstacles.
				for (int i=0; i<environment.getNumActiveObstacles(); i++){
					Obstacle o = environment.getActiveObstacle(i);
					
					Shape shape = o.getShape();
					Color col = o.getColor();
					g2d.setColor(col);
					g2d.fill(shape);
					g2d.setColor(Color.BLACK);
					g2d.draw(shape);
					
					String str = String.valueOf((int)o.index);
					g2d.drawString(str, (int)o.pos_x, (int)o.pos_y);
				}
			}
		}
	}
}