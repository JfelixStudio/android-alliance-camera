package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import alliance.camera.R;
import android.alliance.dialoge.ResolutionDialog;
import android.alliance.exceptions.AllianceExceptionType;
import android.alliance.exceptions.OnException;
import android.alliance.helper.AutoFocusHelper;
import android.alliance.helper.AutoFocusMode;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashMode;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
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
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class UICameraActivity extends Activity implements IAllianceOrientationChanged, IAllianceCameraListener, OnClickListener, OnException {

	protected ImageView in2;
	protected ImageView ib1;
	protected ImageView ivShutter;

	protected ImageView ivResolutionDialog;
	protected ImageView ivAutofocus;
	protected ImageView ibLeft2;
	protected ImageView ivFlashlight;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1
	 */
	protected Integer cameraFacing = null;

	protected boolean useAlternativeFacing = false;

	protected AllianceCamera allianceCamera;
	protected LinearLayout layoutZoom;

	protected ImageView ivZoomOut;
	protected ImageView ivZoomIn;
	
	protected int activityResultCode = RESULT_CANCELED;
	protected SurfaceView surfaceView;
	private AutoFocusHelper autofocusHelper;
	private Activity parentActivity;
	private OnException onException;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#", "onCreate()");

		this.parentActivity = this;
	
		onException = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			cameraFacing = extras.getInt(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
			useAlternativeFacing = extras.getBoolean(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
			
		} else {
			cameraFacing = CameraInfo.CAMERA_FACING_BACK;
			useAlternativeFacing = true;	
		}
		
		setContentView(R.layout.activity_uicamera);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);
		
		String folderPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamTest/";
		File x = new File(folderPath);
		x.mkdirs();
		String fileName = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";

		File filePath = new File(folderPath, fileName);
		
		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing, filePath, this);
		allianceCamera.setPictureSizeMegapixel(3000000);
		allianceCamera.setInitCloseAfterShut(false);
		allianceCamera.setGps(true);
		
		FlashlightHelper flashlightHelper = new FlashlightHelper(this);
		flashlightHelper.addToSequence(FlashMode.FLASH_OFF);
		flashlightHelper.addToSequence(FlashMode.FLASH_AUTO);
		flashlightHelper.addToSequence(FlashMode.FLASH_ON);
		flashlightHelper.addToSequence(FlashMode.FLASH_TORCH);
		
		allianceCamera.setInitFlashlightHelper(flashlightHelper, -1);
		
		
		autofocusHelper = new AutoFocusHelper(this);
		autofocusHelper.addToSequence(AutoFocusMode.AUTO);
		autofocusHelper.addToSequence(AutoFocusMode.OFF);
		autofocusHelper.setStartingMode(AutoFocusMode.AUTO);
		allianceCamera.setAutoFocusHelper(autofocusHelper);
		// for the manual trigger of the auto focus
		surfaceView.setOnClickListener(this);
		
		
		allianceCamera.setInitZoomHelper(new ZoomHelper());
		
		layoutZoom = (LinearLayout) findViewById(R.id.layoutZoom);
		
		in2 = (ImageView) findViewById(R.id.iv2);
		
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
		
		ivAutofocus = (ImageView) findViewById(R.id.ivAutofocus);
		ivAutofocus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				allianceCamera.autofocusHelper.next(allianceCamera.getCamera(), ivAutofocus);
			}
		});

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
		
		ivFlashlight = (ImageView) findViewById(R.id.ivFlashlight);
		ivFlashlight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					Parameters param = allianceCamera.getCameraParameters();
					allianceCamera.flashlightHelper.next(param, ivFlashlight);
					allianceCamera.setCameraParameters(param);	
				} catch (Exception e){
					fireOnException(e, getResources().getString(R.string.exception_flashlight) + " " + UICameraActivity.class.toString() + " ivFlashLight onClick(View v)", AllianceExceptionType.FLASHLIGHT_EXCEPTION);
				}
			}
		});

		allianceCamera.addOrientationChangedListeners(this);

		initFlashlight();
		initAutoFocus();
        
	}

	public void initFlashlight() {
		if (!allianceCamera.flashlightHelper.available) {
			ivFlashlight.setVisibility(View.GONE);
		} else {
			ivFlashlight.setImageResource(allianceCamera.flashlightHelper.flashStatus.drawable);
		}
	}
	
	public void initAutoFocus(){
		if(!allianceCamera.autofocusHelper.available){
			ivAutofocus.setVisibility(View.GONE);
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
		
		rotateView(in2, degree);
		rotateView(ib1, degree);
		rotateView(ivShutter, degree);

		// rotateView(ibLeft0, degree); // not rotated to see the difference
		rotateView(ivAutofocus, degree);
		rotateView(ibLeft2, degree);
		rotateView(ivFlashlight, degree);
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
						try{
							Parameters param = allianceCamera.zoomHelper.zoomIn(allianceCamera.getCameraParameters()); 
							allianceCamera.setCameraParameters(param);	
						} catch (Exception e){
							fireOnException(e, getResources().getString(R.string.exception_zoom) + " " + UICameraActivity.class.toString() + " ivZoomIn - onClick(View v)", AllianceExceptionType.ZOOM_EXCEPTION);
						}
					}
				});
				
				ivZoomOut = new ImageView(this);	
				ivZoomOut.setScaleType(ScaleType.FIT_CENTER);
				ivZoomOut.setImageResource(R.drawable.bt_zoom_out_selector);
				ivZoomOut.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try{
							Parameters param = allianceCamera.zoomHelper.zoomOut(allianceCamera.getCameraParameters()); 
							allianceCamera.setCameraParameters(param);
						} catch(Exception e){
							fireOnException(e, getResources().getString(R.string.exception_zoom) + " " + UICameraActivity.class.toString() + " ivZoomOut - onClick(View v)", AllianceExceptionType.ZOOM_EXCEPTION);
						}
					}
				});
				
				layoutZoom.addView(ivZoomOut);
				layoutZoom.addView(ivZoomIn);	
			}
		}	
	}

	@Override
	public void onClick(View view) {
		if(view == surfaceView){
			if(autofocusHelper.available){
				autofocusHelper.doAutoFocus();
			}
		}
	}

	@Override
	public void onException(Exception exception, String message, AllianceExceptionType type) {
		// TODO Auto-generated method stub
	System.out.println("x");	
	}
	
	private void fireOnException(Exception exception, String message, AllianceExceptionType type){
		if(onException != null){
			onException.onException(exception, message, type);
		}
	}
}
