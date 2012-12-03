package android.alliance.sensors;

import alliance.sensors.R;
import android.alliance.sensors.average.IAverage;
import android.alliance.sensors.average.LowPassAverage;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class YAchseActivity extends Activity {

	private SensorManager mSensorManager;
    
    private TextView txValue;
    private TextView txValueOrientation;
    private TextView txValueMag;
	private TextView txValueAcc;
	private TextView tvConsole;
    
    private SensorEventListener listenerOrientation = null;
	private Sensor sensorOrientation;
	private float mOrientationValues[] = new float[3];
	
	private Sensor sensorAccelerometer;
	private Sensor sensorMagnetometer;
	
	private float[] mGravity = new float[3];
	private IAverage averageGravity = new LowPassAverage(0.2f);
//	private IAverage averageGravity = new MovingAverage(7);
	
    float[] mGeomagnetic = new float[3];
    
    float[] mOrientationRadian = new float[3];
    float[] mOrientationDegree = new float[3];
    private float[] valuesDelta = new float[3];
	private float[] valuesOldPeak = new float[3];
    
	static final float TRESHOLD = 0.31f;

	private SensorEventListener listenerAccelerometer;

	private SensorEventListener listenerMagnetometer;

	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yachse);

        txValue = (TextView) findViewById(R.id.yValue);
        txValueOrientation = (TextView) findViewById(R.id.valueOr);
        txValueMag= (TextView) findViewById(R.id.valueMag);
        txValueAcc = (TextView) findViewById(R.id.valueAcc);
        tvConsole = (TextView) findViewById(R.id.tvConsole);
        tvConsole.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				SensorApplication.getInstance().clearConsole(tvConsole);
				return true;
			}
		});
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        /*
         *
         * TODO: Seit wann deprecated, und warum
         */
        sensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        listenerOrientation = new SensorEventListener(){
			public void onSensorChanged(SensorEvent event) {
				 for (int i=0 ; i<3 ; i++) {
                     mOrientationValues[i] = event.values[i];
                 }
                 txValue.setText(String.valueOf(mOrientationValues[0]));
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        
        
        sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listenerAccelerometer = new SensorEventListener(){

			public void onSensorChanged(SensorEvent event) {
	//				mGravity = event.values;
					averageGravity.getAverage(event.values, mGravity);
					
					txValueAcc.setText(String.valueOf(mGravity[0] + "\n" + mGravity[1] + "\n" + mGravity[2]));
					
					calculateOrientation();
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        
        sensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        listenerMagnetometer = new SensorEventListener(){

			public void onSensorChanged(SensorEvent event) {
				mGeomagnetic = event.values;
				
				txValueMag.setText(String.valueOf(mGeomagnetic[0] + "\n" + mGeomagnetic[1] + "\n" + mGeomagnetic[2]));
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        
	 }
	 
	 /**
	  * Gets called just from the accelerometer event becaus the frequency is enough.
	  */
	 private void calculateOrientation() {
		 	// this method is called from 3 independant threads, therefore it should get synchronized 
		 	synchronized (this) {
	    		float R[] = new float[9];
	    		boolean success = SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic);
	        
	    		if(success) {
	    			/*  azimuth/yaw - z - nose left or right, axis from ground to sky 
	    			 *  pitch - x - nose up or down, axis from wing to wing
	    			 *  roll - y - rotation about an axis running from nose to tail
	    			 *  http://en.wikipedia.org/wiki/Aircraft_principal_axes
	    			 */
	    			float orientation[] = new float[3];
	    			mOrientationRadian = SensorManager.getOrientation(R, orientation);
	    			radianToDegree(mOrientationRadian, mOrientationDegree);
	    			
	    			txValueOrientation.setText("azimuth Z: "+ mOrientationDegree[0] + "\npitch      X: " + mOrientationDegree[1] + "\nroll        Y: " + mOrientationDegree[2]);
	    			
	    			delta(valuesOldPeak, mOrientationRadian, valuesDelta);
	    			
	    			if(valuesDelta[0] > TRESHOLD || valuesDelta[1] > TRESHOLD || valuesDelta[2] > TRESHOLD) {
						SensorApplication.getInstance().addConsoleLine("dx:" + valuesDelta[0] + "  dy:" + valuesDelta[1] + "  dz:" + valuesDelta[2], tvConsole);
						valuesOldPeak = mOrientationRadian.clone();
					}
	    		}
		 	}
	 }
	 
	 /** 
	  * Converts the unit radian to degree. Pi = 3.1415... = 180°
	  * http://en.wikipedia.org/wiki/Radian 
	  */
	 private float radianToDegree(float radian) {
		 return radian*(180/3.1415926f);
	 }
	 
	 /** 
	  * Converts the unit radian to degree. Pi = 3.1415... = 180°
	  * http://en.wikipedia.org/wiki/Radian 
	  */
	 private float[] radianToDegree(float[] radians, float[] degrees) {
		 
		 degrees[0] = radians[0]*(180/3.1415926f);
		 degrees[1] = radians[1]*(180/3.1415926f);
		 degrees[2] = radians[2]*(180/3.1415926f);
		 
		 return degrees;
	 }
	 
	@Override
    protected void onResume() {
        super.onResume();
        
        mSensorManager.registerListener(listenerOrientation, sensorOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(listenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(listenerMagnetometer, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
	
	    
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(listenerOrientation);
        mSensorManager.unregisterListener(listenerAccelerometer);
        mSensorManager.unregisterListener(listenerMagnetometer);
        super.onPause();
    }

    
    private void delta(float[] a, float[] b, float[] delta) {
		for ( int i=0; i<a.length; i++ ) {
			delta[i] = Math.abs(a[i] - b[i]);
		}
	}
}
