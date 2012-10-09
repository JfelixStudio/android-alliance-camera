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
public class CameraNew extends Activity implements Callback {

	/** Intent key to send the initial camera facing. <br>
	 * intent.putExtra(CameraNew.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_FRONT); */
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
	private int cameraFacing = CameraInfo.CAMERA_FACING_BACK;

	
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
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
	

	// TODO: Auswahl des Facings - über Intent?
	// TODO: berücksichtigen das es keine front_facing gibt oder nur eine front_facing(nexus)!
	// TODO: wo ist das exception handling am sinnvollsten?
	private void initCamera(SurfaceHolder holder) {
		try {

			// wiso back cam kann ja auch nur die front sein?
			boolean backCamAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

			if (backCamAvailable) {

				if (camera == null) {
					// doc: Returns the number of physical cameras available on this device.
					int numberOfCameras = Camera.getNumberOfCameras();
					
					// numberOfCameras doesn't indicates the facing - http://digitaldumptruck.jotabout.com/?p=797
					for (int cameraIdx = 0; cameraIdx < numberOfCameras; cameraIdx++) {
						// doc: Returns the information about a particular camera.
						Camera.getCameraInfo(cameraIdx, cameraInfo);
						if (cameraInfo.facing == cameraFacing) {
							try {
								// doc: Creates a new Camera object to access a particular hardware camera. If the same camera is opened by other applications, this will throw a RuntimeException.
								camera = Camera.open(cameraIdx);
							} catch (RuntimeException e) {
								Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
								Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
							}
						}
					}
				}

				camera.setPreviewDisplay(holder);
			}

		} catch (Exception e) {
			camRelease();

			/**
			 * TODO Hier Kamera nochmal rekursiv neu initialisieren, oder aber
			 * einen Fehlermeldung zurückgeben, dass sie nicht richtig
			 * initialisiert wurde
			 */
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

	
}
