import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.text.DecimalFormat;
import java.util.*;


public class ButtonPanel extends JPanel implements ActionListener {
		Dimension preferredSize = new Dimension(320, 650);
		static Simulation scape;
		Agent[] agents;
		JTextPane info;
		JPanel buttons1, buttons2, buttons3, body;
		JLabel showEpochs, forwardLabel;
		JTextField forwardEpochs;
		JButton next, forward, restart, exit;
		String[] colors = { "red", "blue", "green", "magenta", "cyan", "black" };
		private String newline = "\n";
		String content[] = {"Individual Ranking",
							"      id\t" + "strat\t" + "score / games  (average)" ,
							"1. ",
							"2. ",
							"3. ",
							"4. ",
							"5. ",
							"6. ",
							"7. ",
							"8. ",
							"9. ",
							"10. ",
							"..." + newline,
							"Strategy Ranking",
							"     agents\t" + "strat\t" + "score / games (average)",
							"1. ",
							"2. ",
							"3. ",
							"4. ",
							"5. ",
							"6. ",
							newline + "Agent",
							"ID:",
							"Strategy:",
							"Score:",
							"Encounters:"
		};
		
		String style[] = {	"bold_italic",
							"bold",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"bold_italic",
							"bold",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"regular",
							"bold",
							"regular",
							"regular",
							"regular",
							"regular"
							
		};
		
		
		
		public ButtonPanel(Simulation controller) {
			this.scape = controller;
			this.agents = scape.agents;
			this.setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			info = new JTextPane();
			info.setPreferredSize(new Dimension(270, 450));
			info.setMaximumSize(new Dimension(270, 450));
			info.setEditable(false);
			info.setOpaque(false);
			info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0), BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEmptyBorder(5, 5, 5, 0))));
			StyledDocument doc = info.getStyledDocument();
	        addStylesToDocument(doc);
	        updateInfo();
			
			showEpochs = new JLabel("", SwingConstants.CENTER);
			String ep = "Epochs: " + scape.epochs;
			showEpochs.setText(ep);
			
			buttons1 = new JPanel();
			buttons1.setLayout(new FlowLayout());
			buttons1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			
			next = new JButton("Next");
			next.setActionCommand("next");
			next.addActionListener(this);
			
			buttons1.add(next);

			forwardLabel = new JLabel("Enter the number of epochs to forward.", SwingConstants.LEFT);
			forwardLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			
			buttons2 = new JPanel();
			buttons2.setLayout(new BoxLayout(buttons2, BoxLayout.LINE_AXIS));
			
			forwardEpochs = new JTextField("0");
			forwardEpochs.setMaximumSize(new Dimension(100, 25));
			
			forward = new JButton("Forward");
			forward.setActionCommand("forward");
			forward.addActionListener(this);
			
			buttons2.add(forwardEpochs);
			buttons2.add(Box.createRigidArea(new Dimension(5, 0)));
			buttons2.add(forward);
			
			buttons3 = new JPanel();
			buttons3.setLayout(new BoxLayout(buttons3, BoxLayout.LINE_AXIS));
			buttons3.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
						
			restart = new JButton("Restart");
			restart.setActionCommand("restart");
			restart.addActionListener(this);
			
			exit = new JButton("Exit");
			exit.setActionCommand("exit");
			exit.addActionListener(this);
			
			buttons3.add(Box.createHorizontalGlue());
			buttons3.add(restart);
			buttons3.add(Box.createRigidArea(new Dimension(5, 0)));
			buttons3.add(exit);
			
			body = new JPanel();
			body.setLayout(new GridLayout(0, 1));
			
			body.add(showEpochs);
			body.add(buttons1);
			body.add(forwardLabel);
			body.add(buttons2);
			
			this.add(info, BorderLayout.NORTH);
			this.add(body, BorderLayout.CENTER);
			this.add(buttons3, BorderLayout.SOUTH);
		}
		
        public Dimension getPreferredSize() {
            return preferredSize;
        }
        
		public void actionPerformed(ActionEvent e) {
			if("next".equals(e.getActionCommand())) {
				this.update(1);
			}
			
			if("forward".equals(e.getActionCommand())) {
				Integer num = new Integer(forwardEpochs.getText());
				this.update(num.intValue());
			}
			
			if("restart".equals(e.getActionCommand())) {
				scape.frame.dispose();
				scape.setup();
			}
			
			if("exit".equals(e.getActionCommand())) {
				scape.frame.dispose();
			}
		}
		
		private void update(int cycles) {
			for(int c = 0; c < cycles; c++) {
				scape.step();
				addInfo(scape.grid[scape.mainPanel.xSelected][scape.mainPanel.ySelected]);
				scape.epochs++;
				String ep = "Epochs:  " + scape.epochs;
				showEpochs.setText(ep);
				scape.mainPanel.update();
			}
		}
		
		private void addStylesToDocument(StyledDocument doc) {
	        //Initialize some styles.
	        Style def = StyleContext.getDefaultStyleContext().
	                        getStyle(StyleContext.DEFAULT_STYLE);

	        Style regular = doc.addStyle("regular", def);
	        StyleConstants.setFontFamily(def, "SansSerif");
	        StyleConstants.setFontSize(regular, 11);
	        
	        Style s = doc.addStyle("bold_italic", regular);
	        StyleConstants.setItalic(s, true);
	        StyleConstants.setBold(s, true);

	        s = doc.addStyle("bold", regular);
	        StyleConstants.setBold(s, true);
	        
	        s = doc.addStyle("red", regular);
	        StyleConstants.setForeground(s, Color.red);
	        
	        s = doc.addStyle("blue", regular);
	        StyleConstants.setForeground(s, Color.blue);
	        
	        s = doc.addStyle("green", regular);
	        StyleConstants.setForeground(s, Color.green);
	        
	        s = doc.addStyle("magenta", regular);
	        StyleConstants.setForeground(s, Color.magenta);
	        
	        s = doc.addStyle("cyan", regular);
	        StyleConstants.setForeground(s, Color.cyan);
	        
	        s = doc.addStyle("black", regular);
	        StyleConstants.setForeground(s, Color.black);
	    }
		
		public void addInfo(Site s) {
			content[0] = "Individual Ranking";
			content[1] = "      id\t" + "strat\t" + "score / games  (average)" ;
			if (agents.length < 10) {
				for (int i = 0; i < agents.length; i++) {
					int num = i + 1;
					content[(i + 2)] = num + ".   " + agents[i].getRanking();
					setColor(i);
				}
			}
			else {
				for(int i = 0; i < 9; i++) {
					int num = i + 1;
					content[(i + 2)] = num + ".   " + agents[i].getRanking();
					setColor(i);
				}
				content[11] = "10.  " + agents[9].getRanking();
				setColor(9);
			}
			content[12] = "..." + newline;
			
			int[] stratRanking = scape.getStrategyRanking();
			
			content[13] = "Strategy Ranking";
			content[14] = "     agents\t" + "strat\t" + "score / games (average)";
			for(int i = 0; i < scape.numStrategies; i++) {
				int num = i + 1;
				double av = scape.averageScoreStrat[stratRanking[i]];
				double enc = scape.averageEncountersStrat[stratRanking[i]];
				content[(15 + i)] = num +  ".   " + scape.agentsStrat[stratRanking[i]] + 
				                  "\t" + scape.strategies[stratRanking[i]] + 
				                  "\t" + round(av) + " / " + round(enc) + " (" + round(av/enc) + ")";
				style[(15 + i)] = colors[stratRanking[i]];
			}
			
			Agent a = s.getAgent();
			if(a != null) {
				content[22] = "ID: " + a.getID();
				content[23] = "Strategy " + a.getStrategy();
				content[24] = "Score: " + a.getScore();
				content[25] = "Encounters: " + a.getEncounters();
			}
			
			else {
				content[22] = "ID: ";
				content[23] = "Strategy: ";
				content[24] = "Score: ";
				content[25] = "Encounters: ";
			}
			
			updateInfo();
		}
		
		public void setColor(int i) {
			String strat = agents[i].getStrategy();
			for(int str = 0; str < scape.strategies.length; str++) {
				if(strat.equals(scape.strategies[str])) {
					style[(i + 2)] = colors[str];
				}
			}
		}
		
		public String round(double value) {
			DecimalFormat df = new DecimalFormat("0.00");
			String stringValue = df.format(value);
			return stringValue;
		}
		
		private void updateInfo() {
			StyledDocument doc = info.getStyledDocument();
			try {
				doc.remove(0, doc.getLength());
	            for (int i=0; i < content.length; i++) {
	                doc.insertString(doc.getLength(), content[i] + newline,
	                                 doc.getStyle(style[i]));
	            }
	        } catch (BadLocationException ble) {
	            System.err.println("Couldn't insert text into text pane.");
	        }
		}
	}
