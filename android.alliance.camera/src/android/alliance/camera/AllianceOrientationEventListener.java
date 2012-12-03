package android.alliance.camera;

import java.util.ArrayList;
import java.util.List;

import alliance.camera.R;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * Creates a new OrientationEventListener.
 * 
 * @param context
 *            for the OrientationEventListener.
 * @param rate
 *            at which sensor events are processed (see also
 *            {@link android.hardware.SensorManager SensorManager}). Use the
 *            default value of
 *            {@link android.hardware.SensorManager#SENSOR_DELAY_NORMAL
 *            SENSOR_DELAY_NORMAL} for simple screen orientation change
 *            detection.
 */
public class AllianceOrientationEventListener extends OrientationEventListener {

	// Is set to max to generate an inital onAllianceOrientationChanged() callback that can be used to align the ui-buttons
	private int mOrientation = Integer.MAX_VALUE;
	private int cameraId = CameraInfo.CAMERA_FACING_BACK;
	
	private List<IAllianceOrientationChanged> orientationChangedListeners = new ArrayList<IAllianceOrientationChanged>();
	
	public AllianceOrientationEventListener(Context context, int rate) {
		super(context, rate);
	}

	/*
	 * Called when the orientation of the device has changed. Orientation
	 * parameter is in degrees, ranging from 0 to 359. 0 degrees when the
	 * device is oriented in its natural position.
	 */
	@Override
	public void onOrientationChanged(int orientation) {
		if (orientation == ORIENTATION_UNKNOWN)
			return;

		int orientationType = (orientation + 45) / 90 * 90;
		if (orientationType == 360) {
			orientationType = 0;
		}

		if (mOrientation != orientationType) {
			Log.d("#", "AllianceOrientationEventListener.orientation = " + orientation);
			Log.d("#", "AllianceOrientationEventListener.orientationType = " + orientationType);

			mOrientation = orientationType;

			// TODO: cameraId muss noch belegt werden
			CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(cameraId, info);

			int rotation = 0;
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				rotation = (info.orientation + orientationType - 180) % 360;
			} else { // back-facing camera
				rotation = (info.orientation + orientationType) % 360;
			}
			Log.d("#", "AllianceOrientationEventListener.rotation = " + rotation);

			fireOrientationChangedListeners(orientation, orientationType, rotation);
		}
	}

	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}
	
	public void addOrientationChangedListeners(IAllianceOrientationChanged listener) {
		orientationChangedListeners.add(listener);
	}
	
	private void fireOrientationChangedListeners(int orientation, int orientationType, int rotation) {
		for(IAllianceOrientationChanged listener : orientationChangedListeners) {
			listener.onAllianceOrientationChanged(orientation, orientationType, rotation);
		}
	}
}
