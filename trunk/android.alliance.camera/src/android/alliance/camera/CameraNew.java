package android.alliance.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
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

public class CameraNew extends Activity implements Callback {

	private Context ctx;
	private Camera camera;
	private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	private SurfaceView surfaceView;
	private Parameters parameters;
	private Display display = null;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 */
	private int cameraFacing = cameraInfo.CAMERA_FACING_BACK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		// Muß aufgerufen werden, bevor Inhalte der Kamera zugewiesen werden
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		setContentView(R.layout.cameranew);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);
		surfaceView.getHolder().addCallback(this);

		/**
		 * Diese Zeile ist echt interessant! Denn wenn man sie auskommentiert,
		 * gibt es kein Preview! Hab eben bestimmt ne viertel Stunde gesucht,
		 * wieso ich kein Preview hatte.
		 * 
		 * Allerdings ist sie deprecated und in der Methode steht:
		 * 
		 * @deprecated this is ignored, this value is set automatically when
		 *             needed.
		 * 
		 *             Da sag ich nur ROFL
		 */
		/*
		 * deprecated setting, but required on Android versions prior to 3.0
		 * source: http://developer.android.com/guide/topics/media/camera.html
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	// TODO: Auswahl des Facings - über Intent?
	// TODO: berücksichtigen das es keine front_facing gibt oder nur eine front_facing(nexus)!
	private void initCamera(SurfaceHolder holder) {
		try {

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

	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		super.onResume();

	}

	@Override
	protected void onStop() {
		Log.d("#", "onStop()");
		super.onStop();
		camRelease();
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
}
