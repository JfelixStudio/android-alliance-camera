package android.alliance.sensors.average;

/**
 * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
 * 
 * @author alliance
 *
 */
public class LowPassAverage implements IAverage {
	
	/*
	 * time smoothing constant for low-pass filter
	 * 0 <= alpha <= 1 ; a smaller value basically means more smoothing
	 */
	static final float ALPHA = 0.15f;
	
	private float alpha = ALPHA; 

	public LowPassAverage(float alpha) {
		this.alpha = alpha;
	}
	
	public float[] getAverage(float[] value, float[] average) {
		
		if(average == null) {
			average = new float[3];
		}
		
		for ( int i=0; i<value.length; i++ ) {
			average[i] = average[i] + ALPHA * (value[i] - average[i]);
	    }
	    return average;
	}

}
