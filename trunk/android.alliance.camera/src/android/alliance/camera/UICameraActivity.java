package android.alliance.camera;

import android.alliance.dialoge.ResolutionDialog;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashLightStatus;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class UICameraActivity extends Activity implements IAllianceOrientationChanged {

	private SurfaceView surfaceView;

//	private float rotation = 0;

	private ImageButton ib0;
	private ImageButton ib1;
	private ImageButton ib2;

	private ImageView ivResolutionDialog;
	private ImageButton ibLeft1;
	private ImageButton ibLeft2;
	private ImageView ibFlashlight;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1
	 */
	private Integer cameraFacing = null;

	private boolean useAlternativeFacing = false;

	private AllianceCamera allianceCamera;
	private LinearLayout layoutZoom;
	
	
	// Activity livecycle ///////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			cameraFacing = extras.getInt(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
			useAlternativeFacing = extras.getBoolean(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
		}
		
		setContentView(R.layout.activity_uicamera);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);

		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing);

		layoutZoom = (LinearLayout) findViewById(R.id.layoutZoom);
		
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

		ivResolutionDialog = (ImageView) findViewById(R.id.ivResolution);
		ivResolutionDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(ResolutionHelper.getInstance().lSupportedPictureSizes.size() > 0){
					ResolutionDialog resDialog = new ResolutionDialog(UICameraActivity.this);
					resDialog.show();
				}
			}
		});
		
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
		ibFlashlight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Parameters param = allianceCamera.getCameraParameters();
				FlashlightHelper.getInstance().nextFlashMode(param, ibFlashlight);
				allianceCamera.setCameraParameters(param);
			}
		});

		allianceCamera.addOrientationChangedListeners(this);

		initFlashlight();
        
	}

	private void initFlashlight() {

		FlashlightHelper.getInstance().init(this);
		
		if (!FlashlightHelper.getInstance().available) {
			ibFlashlight.setVisibility(View.GONE);
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
		rotateView(ivResolutionDialog, degree);
		
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

	public void createZoomButtons(){

		ZoomHelper zoomHelper = ZoomHelper.getInstance();
		
		if(zoomHelper.mSmoothZoomSupported){
			
			
		} else {
			
			ImageView zoomIn = new ImageView(this);
			zoomIn.setScaleType(ScaleType.FIT_CENTER);
			zoomIn.setImageResource(R.drawable.bt_zoom_in_default_48);
			zoomIn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Parameters param = ZoomHelper.getInstance().zoomIn(allianceCamera.getCameraParameters()); 
					allianceCamera.setCameraParameters(param);
				}
			});
			
			ImageView zoomOut = new ImageView(this);	
			zoomOut.setScaleType(ScaleType.FIT_CENTER);
			zoomOut.setImageResource(R.drawable.bt_zoom_out_default_48);
			zoomOut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Parameters param = ZoomHelper.getInstance().zoomOut(allianceCamera.getCameraParameters()); 
					allianceCamera.setCameraParameters(param);
				}
			});
			
			layoutZoom.addView(zoomIn);
			layoutZoom.addView(zoomOut);	
		}
	}
}
