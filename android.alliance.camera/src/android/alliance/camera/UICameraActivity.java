package android.alliance.camera;

import android.alliance.dialoge.ResolutionDialog;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.LinearLayout;

public class UICameraActivity extends Activity implements IAllianceOrientationChanged, IAllianceCameraListener {

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

	private ImageView ivZoomOut;

	private ImageView ivZoomIn;
	private FlashlightHelper flashlightHelper = new FlashlightHelper();
	private ZoomHelper zoomHelper = new ZoomHelper();
	
	
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

		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing, flashlightHelper, zoomHelper);

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
				flashlightHelper.nextFlashMode(param, ibFlashlight);
				allianceCamera.setCameraParameters(param);
			}
		});

		allianceCamera.addOrientationChangedListeners(this);

		initFlashlight();
        
	}

	private void initFlashlight() {

		flashlightHelper.init(this);
		
		if (!flashlightHelper.available) {
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
		allianceCamera.addAllianceCameraListener(this);
		super.onResume();
	}
	
	@Override
	public void onCameraCreated() {
		createZoomButtons();
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
		
		rotateView(ivZoomIn, degree);
		rotateView(ivZoomOut, degree);
		
	}
	
	private void rotateView(View view, float degree) {
		// check to null, if zoom buttons not initialize
		if(view != null){
			// TODO: 90° kommen von der landscape orientation und sollten dynamisch
			// ausgelesen werden
			Animation an = new RotateAnimation(0.0f, -degree, view.getWidth() / 2, view.getHeight() / 2);

			an.setDuration(0);
			an.setRepeatCount(0);
			an.setFillAfter(true);

			view.startAnimation(an);			
		}

	}

	@Override
	public void onAllianceOrientationChanged(int orientation, int orientationType, int rotation) {
		orientationHasChanged(rotation);
	}

	// called from AllianceCamera
	public void createZoomButtons() {

		if(zoomHelper.mSmoothZoomSupported){
			
			SeekBar seekBar = new SeekBar(this);
			seekBar.setThumb(getResources().getDrawable(R.drawable.bt_back));
//			Aktueller SmoothZoomWert setzen
//			seekbar.setProgress(new Float(xxx);
			seekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
		        	
	        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            }
	
	            public void onStartTrackingTouch(SeekBar seekBar) { 
	            }
	
	            public void onStopTrackingTouch(SeekBar seekBar){
	            }

			});
			
			layoutZoom.addView(seekBar);
			
		} else if(zoomHelper.mZoomSupported){
			
			ivZoomIn = new ImageView(this);
			ivZoomIn.setScaleType(ScaleType.FIT_CENTER);
			ivZoomIn.setImageResource(R.drawable.bt_zoom_in_default);
			ivZoomIn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Parameters param = zoomHelper.zoomIn(allianceCamera.getCameraParameters()); 
					allianceCamera.setCameraParameters(param);
				}
			});
			
			ivZoomOut = new ImageView(this);	
			ivZoomOut.setScaleType(ScaleType.FIT_CENTER);
			ivZoomOut.setImageResource(R.drawable.bt_zoom_out_default);
			ivZoomOut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Parameters param = zoomHelper.zoomOut(allianceCamera.getCameraParameters()); 
					allianceCamera.setCameraParameters(param);
				}
			});
			
			layoutZoom.addView(ivZoomIn);
			layoutZoom.addView(ivZoomOut);	
		}
	}
}
