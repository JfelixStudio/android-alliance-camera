package android.alliance.orientation;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Inspirations: <br>
 * http://android-developers.blogspot.de/2010/09/one-screen-turn-deserves-another.html
 * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on-android-i-e-get-landscape
 * 
 * @author strangeoptics
 *
 */
public class OrientationActivity extends Activity {

	private TextView tvOnOrientationChanged;
	private TextView tvMyRotation;
	private TextView tvDeviceRotation;
	private TextView tvDisplayMetrics;
	private TextView tvCameraInfoOrientation;
	private TextView tvConsole;
	
	private OrientationEventListener orientationEventListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);
        
        tvOnOrientationChanged = (TextView) findViewById(R.id.tvOnOrientationChanged);
        tvMyRotation = (TextView) findViewById(R.id.tvMyRotation);
        tvDeviceRotation = (TextView) findViewById(R.id.tvDeviceRotation);
        tvDisplayMetrics = (TextView) findViewById(R.id.tvDisplayMetrics);
        tvCameraInfoOrientation = (TextView) findViewById(R.id.tvCameraInfoOrientation);
        tvConsole = (TextView) findViewById(R.id.tvConsole);
        
        OrientationApplication.getInstance().addConsoleLine("onCreate()", tvConsole);
        
        /* Helper class for receiving notifications from the SensorManager when the orientation of the device has changed.
		 * Used sensor type:			Sensor.TYPE_ACCELEROMETER
		 * SENSOR_DELAY_FASTEST = 0;	get sensor data as fast as possible
		 * SENSOR_DELAY_GAME = 1;		rate suitable for games
		 * SENSOR_DELAY_UI = 2;			rate suitable for the user interface
		 * SENSOR_DELAY_NORMAL = 3;		rate (default) suitable for screen orientation changes
		 */
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
        	
        	/* Called when the orientation of the device has changed.
		     * Orientation parameter is in degrees, ranging from 0 to 359.
		     *   0 degrees when the device is oriented in its natural position.   !!! what is the device's natural position?  http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on-android-i-e-get-landscape
		     *   90 degrees when its left side is at the top. 
		     *   180 degrees when it is upside down. 
		     *   270 degrees when its right side is to the top.
		     * ORIENTATION_UNKNOWN is returned when the device is close to flat and the orientation cannot be determined.
		     */
        	public void onOrientationChanged(int orientation) {
        		
        		if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
        			tvOnOrientationChanged.setText("-1    ORIENTATION_UNKNOWN");
        		} else {
        			tvOnOrientationChanged.setText(Integer.toString(orientation));
        		}
        		
        		
        		
        		// http://android-developers.blogspot.de/2010/09/one-screen-turn-deserves-another.html
        		WindowManager lWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
        		/*
        		 * Returns the rotation of the screen from its "natural" orientation
        		 * The angle is the rotation of the drawn graphics on the screen, which is the opposite direction of the physical rotation of the device.
        		 * Supported since 8 or 2.2 
        		 */
            	int lRotation = lWindowManager.getDefaultDisplay().getRotation();
            	switch(lRotation) {
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
            	 * The size is adjusted based on the current rotation of the display.
            	 */
            	DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int wPix = dm.widthPixels;
                int hPix = dm.heightPixels;
            	tvDisplayMetrics.setText("w: " + wPix + "    h: " + hPix);
            	
            	CameraInfo info = new android.hardware.Camera.CameraInfo();
                Camera.getCameraInfo(0, info);
                tvCameraInfoOrientation.setText(Integer.toString(info.orientation));
            	
            	if(orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            		
	            	// transforms 0-359 to 0, 90, 180, 270, 360
	            	orientation = (orientation + 45) / 90 * 90;
	            	
	            	int myRotation = 0;
	        		String myRotationText = "";
	        		if(orientation == 0 || orientation == 360) {
	        			myRotation = Surface.ROTATION_0;
	        			if(wPix < hPix) {
	        				myRotationText = "Surface.ROTATION_0   np=portrait";
	        			} else {
	        				myRotationText = "Surface.ROTATION_0   np=landscape";
	        			}
	        		} else
	        		if(orientation == 270) {
	        			myRotation = Surface.ROTATION_270;
	        			myRotationText = "Surface.ROTATION_270";
	        		} else
	        		if(orientation == 180) {
	        			myRotation = Surface.ROTATION_180;
	        			myRotationText = "Surface.ROTATION_180";
	        		} else
	        		if(orientation == 90) {
	        			myRotation = Surface.ROTATION_90;
	        			myRotationText = "Surface.ROTATION_90";
	        		}
	        		
	        		tvMyRotation.setText(myRotationText);
            	}
        	}
        };
        orientationEventListener.enable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_orientation, menu);
        return true;
    }
    
    @Override
    protected void onStart() {
    	Log.d("#", "onStart()");
		OrientationApplication.getInstance().addConsoleLine("onStart()", tvConsole);
    	super.onStart();
    }
    
    @Override
	protected void onPause() {
		Log.d("#", "onPause()");
		OrientationApplication.getInstance().addConsoleLine("onPause()", tvConsole);
		super.onPause();
	}
    
    @Override
    protected void onStop() {
    	Log.d("#", "onStop()");
		OrientationApplication.getInstance().addConsoleLine("onStop()", tvConsole);
    	super.onStop();
    }
	
    @Override
    protected void onRestart() {
    	Log.d("#", "onRestart()");
		OrientationApplication.getInstance().addConsoleLine("onRestart()", tvConsole);
    	super.onRestart();
    }
    
	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		OrientationApplication.getInstance().addConsoleLine("onResume()", tvConsole);
		super.onResume();
	}
	
}
