package android.alliance.focus;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorAutoFocus extends AutoFocus implements SensorEventListener {

	private SensorManager mySensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagnetometer;
	
	float[] mGravity;
    float[] mGeomagnetic;
    float[] mOrientation;
	
    float[] mGravityOnLastFocus;
    float[] mGeomagneticOnLastFocus;
    float[] mOrientationOnLastFocus;
	
	public SensorAutoFocus(Camera camera, FocusView focusView, Context ctx) {
		super(camera, focusView);
		
		mySensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
    	
        sensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public void startTask() {
		task = new IntervalAutoFocusAsyncTask();
		task.execute(this, this, this);
	}
	
	@Override
	public void startAutoFocus() {
		mySensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		mySensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_UI);
		
		super.startAutoFocus();
	}
	
	@Override
	public void stopAutoFocus() {
		mySensorManager.unregisterListener(this, sensorAccelerometer);
		mySensorManager.unregisterListener(this, sensorMagnetometer);
		
		super.stopAutoFocus();
	}

	// SensorEventListener ///////////////////
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
    		mGravity = event.values;
    	}
    		
      
    	if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
    		mGeomagnetic = event.values;
    	}
    		
      
    	if (mGravity != null && mGeomagnetic != null) {
    		float R[] = new float[9];
    		float I[] = new float[9];
    		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        
    		if (success) {
    			float orientation[] = new float[3];
    			mOrientation = SensorManager.getOrientation(R, orientation);
    		}
    	}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
