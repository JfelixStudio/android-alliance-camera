package android.alliance.sensors;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class YAchseActivity extends Activity {

	private SensorManager mSensorManager;
    
    private TextView txValue;
    private TextView txValueOrientation;
    private TextView txValueMag;
	private TextView txValueAcc;
    
    private SensorEventListener listenerOrientation = null;
	private Sensor sensorOrientation;
	private float mOrientationValues[] = new float[3];
	
	private Sensor sensorAccelerometer;
	private Sensor sensorMagnetometer;
	
	float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    float[] mOrientation = new float[3];

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
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        /*
         * Hier werden die korrekten Werte ausgelesen
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
        
        
        
        /*
         * Hier werden die falschen Werte ausgelesen 
         */
        sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listenerAccelerometer = new SensorEventListener(){

			public void onSensorChanged(SensorEvent event) {
				mGravity = event.values;
				
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
				
				calculateOrientation();
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        
	 }
	 
	 private void calculateOrientation() {
		 	// this method is called from 3 independant threads, therefore it should get synchronized
		 	synchronized (this) {
	    		float R[] = new float[9];
	    		float I[] = new float[9];
	    		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
	        
	    		if(success) {
	    			float orientation[] = new float[3];
	    			mOrientation = SensorManager.getOrientation(R, orientation);
	    			
	    			txValueOrientation.setText("azimuth Z: "+ mOrientation[0] + "\npitch      X: " + mOrientation[1] + "\nroll        Y: " + mOrientation[2]);
	    		}
		 	}
	 }
	 
	@Override
    protected void onResume() {
        super.onResume();
        
        mSensorManager.registerListener(listenerOrientation, sensorOrientation, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(listenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(listenerMagnetometer, sensorMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }
    
    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(listenerOrientation);
        mSensorManager.unregisterListener(listenerAccelerometer);
        mSensorManager.unregisterListener(listenerMagnetometer);
        super.onStop();
    }

}
