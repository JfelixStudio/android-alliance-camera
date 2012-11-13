package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.alliance.dialoge.ResolutionDialog;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashMode;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class UICameraActivity extends Activity implements IAllianceOrientationChanged, IAllianceCameraListener {

//	private SurfaceView surfaceView;

//	private float rotation = 0;

	private ImageView ib0;
	private ImageView ib1;
	private ImageView ivShutter;

	private ImageView ivResolutionDialog;
	private ImageView ibLeft1;
	private ImageView ibLeft2;
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
	
	private int activityResultCode = RESULT_CANCELED;
	
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

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv_camera);
		
		
		String folderPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamTest/";
		File x = new File(folderPath);
		x.mkdirs();
		String fileName = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";

		File filePath = new File(folderPath, fileName);
		
		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing, filePath);
		allianceCamera.setInitPictureSize(3000000);
		allianceCamera.setInitCloseAfterShut(false);
		
		FlashMode.FLASH_AUTO.drawable = R.drawable.bt_flashlight_auto_selector;
		FlashMode.FLASH_ON.drawable = R.drawable.bt_flashlight_on_selector;
		FlashMode.FLASH_OFF.drawable = R.drawable.bt_flashlight_off_selector;
		FlashMode.FLASH_TORCH.drawable = R.drawable.bt_flashlight_torch_selector;
		
		FlashlightHelper flashlightHelper = new FlashlightHelper(this);
		flashlightHelper.addToSequence(FlashMode.FLASH_AUTO);
		flashlightHelper.addToSequence(FlashMode.FLASH_ON);
		flashlightHelper.addToSequence(FlashMode.FLASH_OFF);
		flashlightHelper.addToSequence(FlashMode.FLASH_TORCH);
		
		allianceCamera.setInitFlashlightHelper(flashlightHelper);
		
		allianceCamera.setInitZoomHelper(new ZoomHelper());
		
		layoutZoom = (LinearLayout) findViewById(R.id.layoutZoom);
		
		ib0 = (ImageView) findViewById(R.id.ib0);
		ib0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		ib1 = (ImageView) findViewById(R.id.ib1);
		
		ivShutter = (ImageView) findViewById(R.id.ivShutter);
		ivShutter.setOnClickListener(new View.OnClickListener() {
			
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

				if(ResolutionHelper.getInstance().supportedPictureSizes.size() > 0){
					ResolutionDialog resDialog = new ResolutionDialog(UICameraActivity.this, R.style.MyResolutionDialog);
					resDialog.show();
				}
			}
		});
		
		ibLeft1 = (ImageView) findViewById(R.id.ibLeft1);
		
		ibLeft2 = (ImageView) findViewById(R.id.ibLeft2);
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
				allianceCamera.flashlightHelper.next(param, ibFlashlight);
				allianceCamera.setCameraParameters(param);
			}
		});

		allianceCamera.addOrientationChangedListeners(this);

		initFlashlight();
        
	}

	public void initFlashlight() {
		if (!allianceCamera.flashlightHelper.available) {
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

		allianceCamera.releaseCamera();
		setResult(activityResultCode);
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
		
		allianceCamera.releaseCamera();
		setResult(activityResultCode);
	}
	
	@Override
	public void afterPhotoTaken() {
		activityResultCode = RESULT_OK;
		setResult(activityResultCode);
	}
	
	// remaining methods ///////////////////////////////////////////////////
	
	private void orientationHasChanged(float degree) {
		System.out.println("orientationHasChanged: " + degree);
		
		rotateView(ib0, degree);
		rotateView(ib1, degree);
		rotateView(ivShutter, degree);

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

		if(allianceCamera.zoomHelper.mZoomSupported){
			if(ivZoomIn == null && ivZoomOut == null){
				ivZoomIn = new ImageView(this);
				ivZoomIn.setScaleType(ScaleType.FIT_CENTER);
				ivZoomIn.setImageResource(R.drawable.bt_zoom_in_selector);
				ivZoomIn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Parameters param = allianceCamera.zoomHelper.zoomIn(allianceCamera.getCameraParameters()); 
						allianceCamera.setCameraParameters(param);
					}
				});
				
				ivZoomOut = new ImageView(this);	
				ivZoomOut.setScaleType(ScaleType.FIT_CENTER);
				ivZoomOut.setImageResource(R.drawable.bt_zoom_out_selector);
				ivZoomOut.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Parameters param = allianceCamera.zoomHelper.zoomOut(allianceCamera.getCameraParameters()); 
						allianceCamera.setCameraParameters(param);
					}
				});
				
				layoutZoom.addView(ivZoomOut);
				layoutZoom.addView(ivZoomIn);	
			}
		}	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == 666){
			// start preview?
		}
	}
}
