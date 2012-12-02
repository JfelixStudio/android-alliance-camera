package android.alliance.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/*
 * http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-gyro
 */
public class GyroActivity extends Activity {

	public static float EPSILON = 1;

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SensorEventListener listenerGyroscope;

	// Create a constant to convert nanoseconds to seconds.
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		listenerGyroscope = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
				// This timestep's delta rotation to be multiplied by the
				// current rotation
				// after computing it from the gyro sample data.
				if (timestamp != 0) {
					final float dT = (event.timestamp - timestamp) * NS2S;
					// Axis of the rotation sample, not normalized yet.
					float axisX = event.values[0];
					float axisY = event.values[1];
					float axisZ = event.values[2];

					// Calculate the angular speed of the sample
					float omegaMagnitude = (float) Math.sqrt(axisX * axisX
							+ axisY * axisY + axisZ * axisZ);

					// Normalize the rotation vector if it's big enough to get
					// the axis
					// (that is, EPSILON should represent your maximum allowable
					// margin of error)
					if (omegaMagnitude > EPSILON) {
						axisX /= omegaMagnitude;
						axisY /= omegaMagnitude;
						axisZ /= omegaMagnitude;
					}

					// Integrate around this axis with the angular speed by the
					// timestep
					// in order to get a delta rotation from this sample over
					// the timestep
					// We will convert this axis-angle representation of the
					// delta rotation
					// into a quaternion before turning it into the rotation
					// matrix.
					float thetaOverTwo = omegaMagnitude * dT / 2.0f;
					float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
					float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
					deltaRotationVector[0] = sinThetaOverTwo * axisX;
					deltaRotationVector[1] = sinThetaOverTwo * axisY;
					deltaRotationVector[2] = sinThetaOverTwo * axisZ;
					deltaRotationVector[3] = cosThetaOverTwo;
				}
				timestamp = event.timestamp;
				float[] deltaRotationMatrix = new float[9];
				SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
						deltaRotationVector);
				// User code should concatenate the delta rotation we computed
				// with the current rotation
				// in order to get the updated rotation.
				// rotationCurrent = rotationCurrent * deltaRotationMatrix;
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(listenerGyroscope, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
    protected void onPause() {
        mSensorManager.unregisterListener(listenerGyroscope);
        super.onPause();
    }

}
