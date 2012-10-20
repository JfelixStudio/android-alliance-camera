package android.alliance.camera;

import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashLightStatus;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

public class UICameraActivity extends Activity implements IAllianceOrientationChanged {

	private SurfaceView surfaceView;

//	private float rotation = 0;

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
	 * CameraInfo.CAMERA_FACING_FRONT = 1
	 */
	private Integer cameraFacing = null;

	private boolean useAlternativeFacing = false;

	private AllianceCamera allianceCamera;
	
	// Activity livecycle ///////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			cameraFacing = extras.getInt(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
			useAlternativeFacing = extras.getBoolean(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
		}

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

				if (view.getVisibility() == View.VISIBLE) {
					view.setVisibility(View.INVISIBLE);
				} else if (view.getVisibility() == View.INVISIBLE) {
					view.setVisibility(View.VISIBLE);
				}
			}
		});

		ibFlashlight = (ImageView) findViewById(R.id.ibFlashlight);
		ibFlashlight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Parameters param = allianceCamera.getCameraParameters();
				/*
				 * Setting Flashlight on Click. If Flashlight: auto => set
				 * status. Check the mode in AllianceCamera on photo capture()
				 * otherwise => set status and set FlashLightType-Mode to
				 * Camera-Parameters
				 */
				if (FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_AUTO)) {
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_ON;
					FlashlightHelper.setFlashMode(param, ibFlashlight);
				} else if (FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_ON)) {
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_OFF;
					FlashlightHelper.setFlashMode(param, ibFlashlight);
				} else if (FlashlightHelper.flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_OFF)) {
					FlashlightHelper.flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
					FlashlightHelper.setFlashMode(param, ibFlashlight);
				}
				allianceCamera.setCameraParameters(param);
			}
		});

		allianceCamera.addOrientationChangedListeners(this);

		initFlashlight();
	}

	private void initFlashlight() {
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

		// TODO: sollte das nicht in onPause()? Ist aber schon in
		// onSurfaceDestroyed
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

		// rotateView(ibLeft0, degree); // not rotated to see the difference
		rotateView(ibLeft1, degree);
		rotateView(ibLeft2, degree);
		rotateView(ibFlashlight, degree);

	}

	private void rotateView(View view, float degree) {
		// TODO: 90° kommen von der landscape orientation und sollten dynamisch
		// ausgelesen werden
		Animation an = new RotateAnimation(0.0f, -degree, view.getWidth() / 2, view.getHeight() / 2);

		an.setDuration(0);
		an.setRepeatCount(0);
		an.setFillAfter(true);

		view.startAnimation(an);
	}

	@Override
	public void onAllianceOrientationChanged(int orientation, int orientationType, int rotation) {
		orientationHasChanged(rotation);
	}

}
