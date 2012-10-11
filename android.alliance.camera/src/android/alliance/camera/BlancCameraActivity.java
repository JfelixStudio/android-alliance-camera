package android.alliance.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author alliance
 *
 */
public class BlancCameraActivity extends Activity implements Callback {

	/** Intent key to send the initial camera facing. <br>
	 * intent.putExtra(BlancCameraActivity.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_FRONT); */
	public static String INTENT_KEY_INITIAL_CAMERA_FACING = "InitialCameraFacing";
	
	private Context ctx;
	private Camera camera;
	private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	private SurfaceView surfaceView;
	private Parameters parameters;
	private Display display = null;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 */
	private Integer cameraFacing = null;

	
	// Activity livecycle ///////////////////////////////
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			cameraFacing = extras.getInt(INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
		}
		
		display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		setContentView(R.layout.cameranew);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);
		surfaceView.getHolder().addCallback(this);

		/*
		 * deprecated setting, but required on Android versions prior to 3.0
		 * source: http://developer.android.com/guide/topics/media/camera.html
		 * 
		 * Ohne diesen Aufruf startet das Preview nicht
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}
	

	
	// SurfaceHolder.Callback ////////////////////////////////
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("#", "surfaceCreated()");
		initCamera(holder);
		initCameraPreferences();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d("#", "surfaceChanged(format=" + format + ", width=" + width + ", height=" + height + ")");
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("#", "surfaceDestroyed()");
		camRelease();
	}

	
	// remaining methods ///////////////////////////////////////////////////
	

	// TODO: Auswahl des Facings - �ber Intent?
	// TODO: ber�cksichtigen das es keine front_facing gibt oder nur eine front_facing(nexus)!
	// TODO: wo ist das exception handling am sinnvollsten?
	private void initCamera(SurfaceHolder holder) {
		try {

			boolean backCamAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
			boolean frontCamAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

			boolean initBackCamera = false;
			boolean initFrontCamera = false;
			
			/**
			 * CAMERAFACING == NULL
			 * 	 Hier wird die R�ck-Kamera genommen, wenn diese nicht vorhanden ist, wird die Front-Kamera genommen
			 * 
			 * CAMERAFACING == CAMERA_FACING_BACK
			 *   Es wird nur die R�ck-Kamera genommen. Falls diese nicht zur Verf�gung steht mu� ein Dialog ausgegeben werden
			 *   
			 * CAMERAFACING == CAMERA_FACING_FRONT
			 *   Es wird nur die Front-Kamera genommen. Falls diese nicht zur Verf�gung steht mu� ein Dialog ausgegeben werden
			 */
			if(cameraFacing == null){
				initBackCamera = true;
				initFrontCamera = true;
			
			} else if(cameraFacing == CameraInfo.CAMERA_FACING_BACK){
				initBackCamera = true;
				initFrontCamera = false;
				
			} else if(cameraFacing == CameraInfo.CAMERA_FACING_FRONT){
				initBackCamera = false;
				initFrontCamera = true;
			}

			
			if(initBackCamera == true && initFrontCamera == false){
				if(backCamAvailable){
					camera = Camera.open();
					
				} else {
					/**
					 *  TODO: Dialog, das die R�ck-Kamera nicht zur Verf�gung steht 
					 */
				}
			
			} else if(initBackCamera == true && initFrontCamera == true){
				
				if(!backCamAvailable){
					if(frontCamAvailable){
						initCameraFront();
					}
					
				} else {
					camera = Camera.open();
				}

			} else if(initBackCamera == false && initFrontCamera == true){
				
				if(frontCamAvailable){
					initCameraFront();
				} else {
					/**
					 * TODO: Dialog, das die Front-Kamera nicht zur Verf�gung steht 
					 */
				}
			}
			

			if(camera != null){
				camera.setPreviewDisplay(holder);	
			}
			
		} catch (Exception e) {
			camRelease();

			Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
			Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
	}

	// Hier werden die Kameras ausgelesen und die
	// Frontkamera initialisiert		
	private void initCameraFront(){
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(camIdx);
			}
		}
	}
	
	
	private void initCameraPreferences() {
		if (camera != null) {

			camera.stopPreview();

			parameters = camera.getParameters();

			parameters.setPictureFormat(ImageFormat.JPEG);

			// Setze BestPreviewSize
			// Setze BestPictureSize
			// Init Autofocus

			camera.setParameters(parameters);

			camera.startPreview();
		}
	}

	private void camRelease() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben
			camera = null;
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
		
		// TODO: sollte das nicht in onPause()?
		camRelease();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}
	
}