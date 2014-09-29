
public class StrategyRanker implements Comparable {
	private int strategy;
	private double average;

	public StrategyRanker(int i, double d, double e) {
		strategy = i;
		
		if(e > 0) 
			average = d/e;
		else 
			average = 0;
	}
	
    public int compareTo(Object s) {
    	double difference = ((StrategyRanker)s).getAverage() - average;
    	if(difference == 0) {
    		return 0;
    	}
    	else if (difference > 0) {
    		return 1;
    	}
    	else {
    		return -1;
    	}
    }

	public double getAverage() {
		return average;
	}

	public int getStrategy() {
		return strategy;
	}
}
