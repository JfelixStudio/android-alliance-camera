package android.alliance.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class AllianceCamera implements Callback {

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
	
	
	public AllianceCamera(Context ctx, SurfaceView surfaceView, int cameraFacing) {
		this.ctx = ctx;
		this.surfaceView = surfaceView;
		this.cameraFacing = cameraFacing;
		
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
		

		// TODO: Auswahl des Facings - über Intent?
		// TODO: berücksichtigen das es keine front_facing gibt oder nur eine front_facing(nexus)!
		// TODO: wo ist das exception handling am sinnvollsten?
		private void initCamera(SurfaceHolder holder) {
			try {

				boolean backCamAvailable = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
				boolean frontCamAvailable = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

				boolean initBackCamera = false;
				boolean initFrontCamera = false;
				
				/**
				 * CAMERAFACING == NULL
				 * 	 Hier wird die Rück-Kamera genommen, wenn diese nicht vorhanden ist, wird die Front-Kamera genommen
				 * 
				 * CAMERAFACING == CAMERA_FACING_BACK
				 *   Es wird nur die Rück-Kamera genommen. Falls diese nicht zur Verfügung steht muß ein Dialog ausgegeben werden
				 *   
				 * CAMERAFACING == CAMERA_FACING_FRONT
				 *   Es wird nur die Front-Kamera genommen. Falls diese nicht zur Verfügung steht muß ein Dialog ausgegeben werden
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
						 *  TODO: Dialog, das die Rück-Kamera nicht zur Verfügung steht 
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
						 * TODO: Dialog, das die Front-Kamera nicht zur Verfügung steht 
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

		public void camRelease() {
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				camera.release(); // Speicher freigeben
				camera = null;
			}
		}

}
