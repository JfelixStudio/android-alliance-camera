package android.alliance.camera;

import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashLightStatus;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

public class UICameraActivity extends Activity {
	
	private Context ctx;
	private SurfaceView surfaceView;
	private Display display = null;
	
	private OrientationEventListener orientationEventListener;
	private float rotation = 0;
	
	private ImageButton ib0;
	private ImageButton ib1;
	private ImageButton ib2;

	private ImageButton ibLeft0;
	private ImageButton ibLeft1;
	private ImageButton ibLeft2;
	private ImageView ibFlashlight;

	private ImageView zoomIn;
	private ImageView zoomOut;
	
	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 */
	private Integer cameraFacing = null;

	private boolean useAlternativeFacing = false;
	
	private AllianceCamera allianceCamera;
	
	
	
	// Activity livecycle ///////////////////////////////
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			cameraFacing = extras.getInt(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
			useAlternativeFacing = extras.getBoolean(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
		}
		
		display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		setContentView(R.layout.activity_uicamera);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);

		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing);
		
		
		ib0 = (ImageButton) findViewById(R.id.ib0);
		ib0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		ib1 = (ImageButton) findViewById(R.id.ib1);
		ib2 = (ImageButton) findViewById(R.id.ib2);
		ib2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				allianceCamera.capture();
			}
		});

		ibLeft0 = (ImageButton) findViewById(R.id.ibLeft0);
		ibLeft1 = (ImageButton) findViewById(R.id.ibLeft1);
		ibLeft2 = (ImageButton) findViewById(R.id.ibLeft2);
		ibLeft2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = findViewById(R.id.leftMenuLevel1);
				
				if(view.getVisibility() == View.VISIBLE) {
					view.setVisibility(View.INVISIBLE);
				} else
				if(view.getVisibility() == View.INVISIBLE) {
					view.setVisibility(View.VISIBLE);
				}
			}
		});
		
		ibFlashlight = (ImageView) findViewById(R.id.ibFlashlight);
		ibFlashlight.setImageResource(R.drawable.bt_flashlight_auto);
		ibFlashlight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 *  Setting Flashlight on Click. 
				 *  If Flashlight:
				 *  	auto 	  => set status. Check the mode in AllianceCamera on photo capture()
				 *  	otherwise => set status and set FlashLight-Mode to Camera-Parameters
				 */
				if(FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_AUTO)){
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_ON;
					ibFlashlight.setImageResource(R.drawable.bt_flashlight_on);
						
					Parameters param = FlashlightHelper.setFlashlightOn(allianceCamera.getCameraParameters());
					allianceCamera.setCameraParameters(param);
						
				} else if(FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_ON)){
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_OFF;
					ibFlashlight.setImageResource(R.drawable.bt_flashlight_off);
					
					Parameters param = FlashlightHelper.setFlashlightOff(allianceCamera.getCameraParameters());
					allianceCamera.setCameraParameters(param);
					
				} else if(FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_OFF)){
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
					ibFlashlight.setImageResource(R.drawable.bt_flashlight_auto);
				}
			}
		});
		
		
		
        
        /* Helper class for receiving notifications from the SensorManager when the orientation of the device has changed.
		 * Used sensor type:			Sensor.TYPE_ACCELEROMETER
		 */
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
        	/* Called when the orientation of the device has changed.
		     * Orientation parameter is in degrees, ranging from 0 to 359.
		     *   0 degrees when the device is oriented in its natural position.
		     *   90 degrees when its left side is at the top. 
		     *   180 degrees when it is upside down. 
		     *   270 degrees when its right side is to the top.
		     * ORIENTATION_UNKNOWN is returned when the device is close to flat and the orientation cannot be determined.
		     */
        	public void onOrientationChanged(int orientation) {
//        		System.out.println("onOrientationChanged(orientation) " + orientation);
        		
        		if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
        			return;
        		
        		// transforms 0-359 to 0, 90, 180, 270, 360
            	orientation = (orientation + 45) / 90 * 90;
            	if(orientation == 360) {
            		orientation = 0;
            	}
            	
//            	System.out.println("orientation: " + orientation);
        		
            	if(rotation != orientation) {
            		rotation = orientation;
            		orientationHasChanged(rotation);
            	}
        	}
        };
        orientationEventListener.enable();
        
        initFlashlight();
	}
	
	private void initFlashlight(){
		boolean available = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if (!available) {
			ibFlashlight.setVisibility(View.GONE);
			FlashlightHelper.flashlightStatus = null;
		} 
	}

	@Override
	protected void onStart() {
		Log.d("#", "onStart()");
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		Log.d("#", "onRestart()");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("#", "onStop()");
		super.onStop();
		
		// TODO: sollte das nicht in onPause()? Ist aber schon in onSurfaceDestroyed
		allianceCamera.camRelease();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}
	
	// remaining methods ///////////////////////////////////////////////////
	
	private void orientationHasChanged(float degree) {
		System.out.println("orientationHasChanged: " + degree);
		
		rotateView(ib0, degree);
		rotateView(ib1, degree);
		rotateView(ib2, degree);

		
//		rotateView(ibLeft0, degree);	// not rotated to see the difference
		rotateView(ibLeft1, degree);
		rotateView(ibLeft2, degree);
		rotateView(ibFlashlight, degree);
		
	}
	
	private void rotateView(View view, float degree) {
		// TODO: 90� kommen von der landscape orientation und sollten dynamisch ausgelesen werden
		Animation an = new RotateAnimation(0.0f, -(degree+90), view.getWidth()/2, view.getHeight()/2);

	    an.setDuration(0);
	    an.setRepeatCount(0);
	    an.setFillAfter(true);
	    
	    view.startAnimation(an);
	}
	

}