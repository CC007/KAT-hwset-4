class Site {

    private int xPosition;
    private int yPosition;
    private Agent agent;
    
    public Site (int x, int y) {
    	xPosition = x;
    	yPosition = y;
    }

    public Agent getAgent() {
    		return agent;
    }
    
    public void setAgent(Agent a) {
    		agent = a; 
    }
    
    public int getXPosition() {
    		return xPosition;
    }
    
    public int getYPosition() {
    		return yPosition;
    }
}