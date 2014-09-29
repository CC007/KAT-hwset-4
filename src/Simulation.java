
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class Simulation {

	// Global scape variables
	static Site[][] grid;
	static int xSize = 50;
	static int ySize = 50;
	static int epochs = 0;
	static Agent[] agents;
	static Random gen = new Random();

	// Strategy variables
	static int startingAgents = 25;
	static String[] strategies = {
		"ALL-D",
		"TIT-FOR-TAT"
		//Add you own strategy names here
	};
	
	static int numAgents;
	static int numStrategies = strategies.length;
	static int[] agentsStrat = new int[numStrategies];
	static double[] averageScoreStrat = new double[numStrategies];
	static double[] averageEncountersStrat = new double[numStrategies];
	
	// Visualization variables
	static JFrame frame;
	static MainPanel mainPanel;
	static ButtonPanel buttonPanel;
	
	
    // Initializing the Scape with Sites.
	private static void initGrid() {
		grid = new Site[xSize][ySize];
		for(int x = 0; x < xSize; x++) {
			for(int y = 0; y < ySize; y++) {
				grid[x][y] = new Site(x, y);
			}
		}
	}
	
	
	// Iniliatizing the Scape with Agents, then assigning each agent to a Site by its x and y co-ordinates.
    private static void initAgents(Simulation controller) {
    	agents = new Agent[numAgents];
    	
    	int id = 0;
    	for(int i = 0; i < numStrategies; i++) {
    		int a = 0;
    		while (a < agentsStrat[i]) {
    			agents[id] = new Agent(controller, id, strategies[i]);
    			id++;
    			a++;
    		}
    		a = 0;
    	}
    	
    	for(int a = 0; a < numAgents; a++) {
    		int x = 0;
    		int y = 0;
    		boolean free = false;
    		while(!free) {
    			x = gen.nextInt(xSize);
    			y = gen.nextInt(ySize);
    			if ((grid[x][y].getAgent()) == null) {
    				free = true;
    			}
    		}
    		
    		agents[a].setPosition(x,y);
    		grid[x][y].setAgent(agents[a]);
    	}
    }
    
    public void step() {
    	
    	for(int a = 0; a < agents.length; a++) {
			agents[a].hasMoved = false;
    	}
    	
    	for(int i = 0; i < numStrategies; i++) {
    		averageScoreStrat[i] = 0;
    		averageEncountersStrat[i] = 0;
    	}
    	
    	for(int a = 0; a < agents.length; a++) {
			agents[a].act();
			
			String strategy = agents[a].getStrategy();
			for(int i = 0; i < numStrategies; i++) {
				if(strategy.equals(strategies[i])) {
					averageScoreStrat[i] += agents[a].getScore();
					averageEncountersStrat[i] += agents[a].getEncounters();
				}
	    	}
	    }
	    
    	for(int i = 0; i < numStrategies; i++) {
    		if(agentsStrat[i] != 0) {
    			averageScoreStrat[i] = averageScoreStrat[i] / (double)agentsStrat[i];
    		}
    		if(averageEncountersStrat[i] != 0) {
    			averageEncountersStrat[i] = averageEncountersStrat[i] / (double)agentsStrat[i];
    		}
    	}
    	
    	Arrays.sort(agents);
    }
    
    public int[] getStrategyRanking() {
    	StrategyRanker[] temp = new StrategyRanker[numStrategies];
    	int[] ranking = new int[numStrategies];
    	
    	for(int i = 0; i < numStrategies; i++) {
    		temp[i] = new StrategyRanker(i, averageScoreStrat[i], averageEncountersStrat[i]);
    	}
    	Arrays.sort(temp);
    	for(int i = 0; i < numStrategies; i++) {
    		ranking[i] = temp[i].getStrategy();
    	}
    	
    	return ranking;
    }
    
    public static void main(String args[]) {
		setup();
	}

    public static void setup() {
		// Construct the setup frame
		final JFrame setup = new JFrame("Welcome!");
		setup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JComponent pane = (JComponent)setup.getContentPane();
		pane.setLayout(new BorderLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel create = new JLabel ("Create your dilemma", SwingConstants.LEFT);
		create.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		JPanel body = new JPanel();
		body.setLayout(new GridLayout(6, 2));
		
		// Contents of the setup frame
		JLabel[] labelStrat = new JLabel[numStrategies];
		final JTextField[] getStrat = new JTextField[numStrategies];
		String content = "" + startingAgents;
		for(int i = 0; i < numStrategies; i++) {
			labelStrat[i] = new JLabel(strategies[i] + ": ");
			getStrat[i] = new JTextField(content);
			
			// Add all the contents to the frame
			body.add(labelStrat[i]);
			body.add(getStrat[i]);
		}
				
		// The ok button is defined here. It ll extract all the information
		// from the content and it will continue to the simulation
		JButton ok = new JButton("Ok");	
		ok.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setup.dispose();
					numAgents = 0;
					for(int i = 0; i < numStrategies; i++) {
						agentsStrat[i] = getValue(getStrat[i].getText());
						numAgents += agentsStrat[i];
					}
					startSimulation();
				}
			}
		);
		
		pane.add(create, BorderLayout.NORTH);
		pane.add(body, BorderLayout.CENTER);
		pane.add(ok, BorderLayout.SOUTH);
		
		setup.pack();
		setup.setLocationRelativeTo(null);
		setup.setSize(300, 300);
        setup.setVisible(true);
	}
    
	public static void startSimulation() {
		epochs = 0;
		
		Simulation controller = new Simulation();
		
		initGrid();
		initAgents(controller);
		createAndShowGUI(controller);
	}
		
	// Function involved in showing the simulation.
    private static void createAndShowGUI(Simulation controller) {
        //Create and set up the window.
        frame = new JFrame("Scape");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //Set up the content pane.
        controller.buildUI(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        buttonPanel.forwardEpochs.grabFocus();
    }
    
    // Function involved in showing the simulation.
    private void buildUI(Container pane) {
        pane.setLayout(new FlowLayout());
        mainPanel = new MainPanel(this);
        pane.add(mainPanel);
        buttonPanel = new ButtonPanel(this);
        pane.add(buttonPanel);
    }
    
    private static int getValue(String s) {
    	if(s.equals("")) {
    		return 0;
    	}
    	else {
    		Integer num = new Integer(s);
    		return num.intValue();
    	}
    }
}

