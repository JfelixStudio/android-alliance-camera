package android.alliance.focus;

import android.alliance.sensor.average.IAverage;
import android.alliance.sensor.average.LowPassAverage;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Triggers the auto focus function for a camera based 
 * on the delta rotation movement of the device. <br>
 * The delta is calculated from the sensors accelerometer
 * and magnetometer. <br>
 * <br>
 * Start the auto focus with <code>startAutoFocus()</code> and stop it 
 * with <code>stopAutoFocus()</code>.
 */
public class SensorAutoFocus extends AutoFocus {

	private SensorManager mySensorManager;
	private Sensor sensorAccelerometer;
	private SensorEventListener listenerAccelerometer;
	private Sensor sensorMagnetometer;
	private SensorEventListener listenerMagnetometer;
	
	private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mOrientation;
    private IAverage averageGravity = new LowPassAverage(0.2f);
	
    private float[] valuesDelta = new float[3];
	private float[] valuesOldPeak = new float[3];
	
	/** Threshold for the delta of the last autofocus position and the actual.
	 * Common value for all axis. */
	private float THRESHOLD = 0.31f;
    
	/**
	 * Instantiates SensorEventListeners for the accelerometer and magnetometer 
	 */
	public SensorAutoFocus(Context ctx, Camera camera, FocusView focusView, Float autoFocusSensibility) {
		super(camera, focusView);
	
		if(autoFocusSensibility != null){
			THRESHOLD = autoFocusSensibility;
		}
		
		mySensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
    	
        sensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listenerAccelerometer = new SensorEventListener() {
        	
			public void onSensorChanged(SensorEvent event) {
					averageGravity.getAverage(event.values, mGravity);
					calculateOrientation();
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
        };
        sensorMagnetometer = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        listenerMagnetometer = new SensorEventListener() {

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
	
	/**
	 * registers sensor listeners (accelerometer, magnetometer)
	 */
	@Override
	public void startAutoFocus() {
		mySensorManager.registerListener(listenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mySensorManager.registerListener(listenerMagnetometer, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		super.startAutoFocus();
	}
	
	/**
	 * registers sensor listeners (accelerometer, magnetometer)
	 */
	@Override
	public void stopAutoFocus() {
		mySensorManager.unregisterListener(listenerAccelerometer, sensorAccelerometer);
		mySensorManager.unregisterListener(listenerMagnetometer, sensorMagnetometer);
		
		super.stopAutoFocus();
	}
	
	/**
	 * Gets called when the accelerometer has a new sensor value.
	 * Calculates the delta between the last position of auto focus
	 * and the actual position. <br>
	 * Is the delta above a certain threshold the auto focus is triggerd to start. 
	 */
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
			
			if(valuesDelta[0] > THRESHOLD || valuesDelta[1] > THRESHOLD || valuesDelta[2] > THRESHOLD) {
				
				doAutoFocus();
				
				valuesOldPeak = mOrientation.clone();	
				// Starts the Autofocus!!!
				
				
			}
		}
		
	}
	
	private void delta(float[] a, float[] b, float[] delta) {
		for ( int i=0; i<a.length; i++ ) {
			delta[i] = Math.abs(a[i] - b[i]);
		}
	}
	
}
