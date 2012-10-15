package android.alliance.sensor.average;

/**
 * http://en.wikipedia.org/wiki/Moving_average
 * 
 * @author alliance
 *
 */
public class MovingAverage implements IAverage {

	private float[][] data;
	
	public MovingAverage(int size) {
		data = new float[size][3];
	}
	
	public float[] getAverage(float[] value, float[] average) {
		
		if(average == null) {
			average = new float[3];
		}
		
		for(int i=0; i<data.length; i++) {
			
			if(i < data.length-1) {
				data[i][0] = data[i+1][0];
				data[i][1] = data[i+1][1];
				data[i][2] = data[i+1][2];
			} else {
				data[i][0] = value[0];
				data[i][1] = value[1];
				data[i][2] = value[2];
			}
			
			average[0] += data[i][0];
			average[1] += data[i][1];
			average[2] += data[i][2];
		}
		
		average[0] = average[0]/data.length;
		average[1] = average[1]/data.length;
		average[2] = average[2]/data.length;
		
		return average;
	}
	
}
