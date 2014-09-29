import java.util.*;

class Agent implements Comparable<Agent> {

    private Random gen = new Random();
    public Simulation scape;

    private int id;
    private int xPosition;
    private int yPosition;
    private int score;
    private int encounters;
    private double average;
    private String strategy;
    private int[][] memory;
    private int[] memoryEncounters;
    private int[][] memoryOwnActions;
    boolean hasMoved;
    private Random r = new Random();

    public Agent(Simulation controller, int i, String s) {
        scape = controller;

        id = i;
        score = 0;
        encounters = 0;
        average = 0;
        strategy = s;
        hasMoved = false;

        //Agent.memory records the past 10 actions of the 
        //encountered agents against this agent. 
        //memory[agentID][0] contains the most recent encounter.
        memory = new int[scape.numAgents][10];
        for (int m = 0; m < memory.length; m++) {
            for (int n = 0; n < memory[m].length; n++) {
                memory[m][n] = 0;
            }
        }

        //Agent.memoryOwnActions records the past 10 actions of this agent 
        //against encountered agents. 
        //memoryOwnActions[agentID][0] contains the most recent encounter.
        memoryOwnActions = new int[scape.numAgents][10];
        for (int m = 0; m < memoryOwnActions.length; m++) {
            for (int n = 0; n < memoryOwnActions[m].length; n++) {
                memoryOwnActions[m][n] = 0;
            }
        }
        
        //memoryEncounters[agentID] contains the amount of encounters with this agent.
        memoryEncounters = new int[scape.numAgents];
        for (int n = 0; n < memoryEncounters.length; n++) {
            memoryEncounters[n] = 0;
        }
    }

    // This method lets agents move and play against other agents.
    public void act() {
        move();
        Vector players = find("players");
        if (players.size() > 0) {
            for (int p = 0; p < players.size(); p++) {
                Agent other = (Agent) players.elementAt(p);
                //The agents only play when *both* have moved.
                if (other.hasMoved) {
                    play(other);
                }
            }
        }
    }

    // 1 equals cooperation, -1 equals defection. 0 means no encounter occurred.
    // When this agent encounters other agents, this method
    // defines the game they will play. 
    public void play(Agent other) {
        int ownAction = getAction(other.getID());
        int otherAction = other.getAction(id);

        // Both agents cooperate.
        if (ownAction == 1 && otherAction == 1) {
            this.score += 3;
            other.addScore(3);
        }

        // This agent cooperates, the other defects.
        if (ownAction == 1 && otherAction == -1) {
            other.addScore(5);
        }

        // This agent defects, the other cooperates.
        if (ownAction == -1 && otherAction == 1) {
            this.score += 5;
        }

        // Both agents defect.
        if (ownAction == -1 && otherAction == -1) {
            this.score += 2;
            other.addScore(2);
        }

        // Below the memories of both agents are updated.
        this.updateMemory(other.getID(), otherAction);
        this.updateMemoryOwnActions(other.getID(), ownAction);

        other.updateMemory(this.id, ownAction);
        other.updateMemoryOwnActions(this.id, otherAction);
    }

    // 1 equals cooperation, -1 equals defection. 0 means no encounter occurred.
    // This function contains two strategies. If you wish to add or remove one,
    // change the strategies array in the Simulation class.
    //
    // You can use action, memory[playerID][n] and memoryOwnActions[playerID][n]
    // Add strategies to variable "static String[] strategies" in Simulation
    public int getAction(int playerID) {
        int action = 1;
        encounters++;
        memoryEncounters[playerID]++;

        //ALL-D
        if (strategy.equals("ALL-D")) {
            action = -1;
        } 
        
        //TIT-FOR-TAT
        else if (strategy.equals("TIT-FOR-TAT")) {
            if (memoryEncounters[playerID] == 1) {
                action = 1;
            } else {
                action = memory[playerID][0];
            }
        } 

        //ALL-C
        else if (strategy.equals("ALL-C")) {
            action = 1;
        } 

        //JOSS
        else if (strategy.equals("JOSS")) {
            if (r.nextInt(10) == 0) {
                action = -1;
            } else if (memoryEncounters[playerID] == 1) {
                action = 1;
            } else {
                action = memory[playerID][0];
            }
        } //TESTER
        else if (strategy.equals("RANDOM")) {
            if (memoryEncounters[playerID] == 1) {
                action = -1;
            } else if (memory[playerID][0] == 1) {
                if (memoryEncounters[playerID] < 4) {
                    action = 1;
                } else if (memoryOwnActions[playerID][0] == -1 || memoryOwnActions[playerID][1] == -1) {
                    action = 1;
                } else {
                    action = -1;
                }
            }else{
                action = memory[playerID][0];
            }
        } //RANDOM
        else if (strategy.equals("RANDOM")) {
            action = (r.nextBoolean() ? 1 : -1);
        }

        return action;
    }

    // Generate a vector with the objects you need. Currently there
    // are two options available: players and free sites.
    public Vector find(String objects) {
        Vector data = new Vector();

        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                int x = xPosition + m;
                int y = yPosition + n;
                if (x < 0) {
                    x += 50;
                }
                if (x >= 50) {
                    x -= 50;
                }
                if (y < 0) {
                    y += 50;
                }
                if (y >= 50) {
                    y -= 50;
                }
                Agent agent = scape.grid[x][y].getAgent();
                if (objects.equals("players") && agent != null && !this.equals(agent)) {
                    data.add(agent);
                }

                if (objects.equals("free sites") && agent == null) {
                    data.add(scape.grid[x][y]);
                }
            }
        }
        return data;
    }

    public void updateMemory(int agentID, int action) {
        for (int m = memory[agentID].length - 2; m >= 0; m--) {
            memory[agentID][m + 1] = memory[agentID][m];
        }
        memory[agentID][0] = action;
    }

    public void updateMemoryOwnActions(int agentID, int action) {
        for (int m = memoryOwnActions[agentID].length - 2; m >= 0; m--) {
            memoryOwnActions[agentID][m + 1] = memoryOwnActions[agentID][m];
        }
        memoryOwnActions[agentID][0] = action;
    }

    // The agent moves 1 site in a random direction.
    // When an Agent moves, tell its old Site it has left, its new Site it has arrived, and itself that it has moved.
    public void move() {
        Vector sites = find("free sites");
        if (sites.size() != 0) {
            int choice = gen.nextInt(sites.size());
            Site nextSite = (Site) sites.elementAt(choice);
            scape.grid[xPosition][yPosition].setAgent(null);
            nextSite.setAgent(this);
            xPosition = nextSite.getXPosition();
            yPosition = nextSite.getYPosition();
        }
        this.hasMoved = true;
    }

    public int getID() {
        return id;
    }

    public void setPosition(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int s) {
        score += s;
    }

    public int getEncounters() {
        return encounters;
    }

    public int compareTo(Agent a) {
        return ((Agent) a).getScore() - score;
    }

    public String getRanking() {
        average = (double) score / (double) encounters;
        return id + "\t" + strategy + "\t" + score + " / " + encounters + "  (" + scape.buttonPanel.round(average) + ") ";
    }
}
