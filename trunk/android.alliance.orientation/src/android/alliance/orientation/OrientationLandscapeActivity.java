package android.alliance.orientation;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class OrientationLandscapeActivity extends Activity {

	private TextView tvOnOrientationChanged;
	private TextView tvDeviceRotation;
	private TextView tvDisplayMetrics;
	private TextView tvConsole;

	private OrientationEventListener orientationEventListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orientation);

		tvOnOrientationChanged = (TextView) findViewById(R.id.tvOnOrientationChanged);
		tvDeviceRotation = (TextView) findViewById(R.id.tvDeviceRotation);
		tvDisplayMetrics = (TextView) findViewById(R.id.tvDisplayMetrics);
		tvConsole = (TextView) findViewById(R.id.tvConsole);

		OrientationApplication.getInstance().addConsoleLine("onCreate()", tvConsole);

		/*
		 * Helper class for receiving notifications from the SensorManager when
		 * the orientation of the device has changed. Used sensor type:
		 * Sensor.TYPE_ACCELEROMETER SENSOR_DELAY_FASTEST = 0; get sensor data
		 * as fast as possible SENSOR_DELAY_GAME = 1; rate suitable for games
		 * SENSOR_DELAY_UI = 2; rate suitable for the user interface
		 * SENSOR_DELAY_NORMAL = 3; rate (default) suitable for screen
		 * orientation changes
		 */
		orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

			/*
			 * Called when the orientation of the device has changed.
			 * Orientation parameter is in degrees, ranging from 0 to 359. 0
			 * degrees when the device is oriented in its natural position. !!!
			 * what ist the device's natural position?
			 * http://stackoverflow.com/questions
			 * /4553650/how-to-check-device-natural
			 * -default-orientation-on-android-i-e-get-landscape 90 degrees when
			 * its left side is at the top. 180 degrees when it is upside down.
			 * 270 degrees when its right side is to the top.
			 * ORIENTATION_UNKNOWN is returned when the device is close to flat
			 * and the orientation cannot be determined.
			 */
			public void onOrientationChanged(int orientation) {

				if (orientation == -1) {
					tvOnOrientationChanged.setText("-1    ORIENTATION_UNKNOWN");
				} else {
					tvOnOrientationChanged.setText(Integer.toString(orientation));
				}

				// http://android-developers.blogspot.de/2010/09/one-screen-turn-deserves-another.html
				WindowManager lWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
				/*
				 * Returns the rotation of the screen from its "natural"
				 * orientation The angle is the rotation of the drawn graphics
				 * on the screen, which is the opposite direction of the
				 * physical rotation of the device.
				 */
				int lRotation = lWindowManager.getDefaultDisplay().getRotation();
				switch (lRotation) {
				case Surface.ROTATION_0:
					tvDeviceRotation.setText(Integer.toString(lRotation) + "   Surface.ROTATION_0");
					break;
				case Surface.ROTATION_90:
					tvDeviceRotation.setText(Integer.toString(lRotation) + "   Surface.ROTATION_90");
					break;
				case Surface.ROTATION_180:
					tvDeviceRotation.setText(Integer.toString(lRotation) + "   Surface.ROTATION_180");
					break;
				case Surface.ROTATION_270:
					tvDeviceRotation.setText(Integer.toString(lRotation) + "   Surface.ROTATION_270");
					break;
				}

				/*
				 * The size is adjusted based on the current rotation of the
				 * display.
				 */
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int wPix = dm.widthPixels;
				int hPix = dm.heightPixels;
				tvDisplayMetrics.setText("w: " + wPix + "    h: " + hPix);
			}
		};
		orientationEventListener.enable();
	}
	
	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		OrientationApplication.getInstance().addConsoleLine("onPause()", tvConsole);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		OrientationApplication.getInstance().addConsoleLine("onResume()", tvConsole);
		super.onResume();
	}
	
}
