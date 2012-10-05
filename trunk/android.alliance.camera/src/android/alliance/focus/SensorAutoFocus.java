package android.alliance.focus;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorAutoFocus extends AutoFocus implements SensorEventListener {

	float[] mGravity;
    float[] mGeomagnetic;
    float[] mOrientation;
	
    float[] mGravityOnLastFocus;
    float[] mGeomagneticOnLastFocus;
    float[] mOrientationOnLastFocus;
	
	public SensorAutoFocus(Camera camera, FocusView focusView) {
		super(camera, focusView);
	}

	@Override
	public void startTask() {
		task = new IntervalAutoFocusAsyncTask();
		task.execute(this, this, this);
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
