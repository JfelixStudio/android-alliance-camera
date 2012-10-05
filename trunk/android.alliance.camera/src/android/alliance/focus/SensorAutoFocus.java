package android.alliance.focus;

import android.hardware.Camera;

public class SensorAutoFocus extends AutoFocus {

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

}
