package android.alliance.focus;

import android.alliance.sensor.average.IAverage;
import android.alliance.sensor.average.LowPassAverage;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorAutoFocus extends AutoFocus {

	private SensorManager mySensorManager;
	private Sensor sensorAccelerometer;
	private SensorEventListener listenerAccelerometer;
	private Sensor sensorMagnetometer;
	private SensorEventListener listenerMagnetometer;
	
	float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    float[] mOrientation;
    private IAverage averageGravity = new LowPassAverage(0.2f);
	
    private float[] valuesDelta = new float[3];
	private float[] valuesOldPeak = new float[3];
	static final float TRESHOLD = 0.31f;
    
	public SensorAutoFocus(Camera camera, FocusView focusView, Context ctx) {
		super(camera, focusView);
		
		mySensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
    	
        sensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listenerAccelerometer = new SensorEventListener(){
			public void onSensorChanged(SensorEvent event) {
					averageGravity.getAverage(event.values, mGravity);
					calculateOrientation();
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        sensorMagnetometer = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        listenerMagnetometer = new SensorEventListener(){

			public void onSensorChanged(SensorEvent event) {
				mGeomagnetic = event.values;
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
	}

	@Override
	public void startTask() {
		
	}
	
	@Override
	public void startAutoFocus() {
		mySensorManager.registerListener(listenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mySensorManager.registerListener(listenerMagnetometer, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		super.startAutoFocus();
	}
	
	@Override
	public void stopAutoFocus() {
		mySensorManager.unregisterListener(listenerAccelerometer, sensorAccelerometer);
		mySensorManager.unregisterListener(listenerMagnetometer, sensorMagnetometer);
		
		super.stopAutoFocus();
	}

	
	private void calculateOrientation() {
		
		float R[] = new float[9];
		boolean success = SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic);
    
		if(success) {
			/*  azimuth/yaw - z - nose left or right, axis from ground to sky 
			 *  pitch - x - nose up or down, axis from wing to wing
			 *  roll - y - rotation about an axis running from nose to tail
			 *  http://en.wikipedia.org/wiki/Aircraft_principal_axes
			 */
			float orientation[] = new float[3];
			mOrientation = SensorManager.getOrientation(R, orientation);
			
			delta(valuesOldPeak, mOrientation, valuesDelta);
			
			if(valuesDelta[0] > TRESHOLD || valuesDelta[1] > TRESHOLD || valuesDelta[2] > TRESHOLD) {
				
				autoFocus();
				
				valuesOldPeak = mOrientation.clone();
			}
		}
		
	}
	
	private void delta(float[] a, float[] b, float[] delta) {
		for ( int i=0; i<a.length; i++ ) {
			delta[i] = Math.abs(a[i] - b[i]);
		}
	}
}
