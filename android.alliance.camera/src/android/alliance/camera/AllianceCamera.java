package android.alliance.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.alliance.focus.MyFocusRectangle;
import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.CameraPreviewSizeHelper;
import android.alliance.helper.Exif;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class AllianceCamera implements Callback, IAllianceOrientationChanged {

	/**
	 * Intent key to send the initial camera facing. <br>
	 * intent.putExtra(BlancCameraActivity.INTENT_KEY_INITIAL_CAMERA_FACING,
	 * CameraInfo.CAMERA_FACING_FRONT);
	 */
	public static String INTENT_KEY_INITIAL_CAMERA_FACING = "InitialCameraFacing";

	/**
	 * Intent key to indicate if the camera can use an alternative facing camera
	 * in case the desired is not available.
	 */
	public static String INTENT_KEY_USE_ALTERNATIVE_FACING = "UseAlternativeFacing";

	private Context ctx;
	private int cameraId;
	private Camera camera;
	private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	private SurfaceView surfaceView;
	private Parameters parameters;
	private IAllianceCameraListener allianceCameraListener;
	
	// private int mOrientation;
	
	private AllianceOrientationEventListener orientationListener;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1
	 * AllianceCamera.CAMERA_FACING_BACK_OR_FRONT
	 */
	private Integer cameraFacing = null;
	private boolean useAlternativeFacing = false;
	
	private SensorAutoFocus sensorAutoFocus;

	private AudioManager audioManager;
	private ResolutionHelper resolutionHelper =  ResolutionHelper.getInstance();
	private FlashlightHelper flashlightHelper;
	private ZoomHelper zoomHelper;
	private File filePath;
	
	public AllianceCamera(Context ctx, SurfaceView surfaceView, int cameraFacing, boolean useAlternativeFacing, FlashlightHelper flashlightHelper, ZoomHelper zoomHelper, File filePath) {
		this.ctx = ctx;
		this.surfaceView = surfaceView;
		this.cameraFacing = cameraFacing;
		this.useAlternativeFacing = useAlternativeFacing;
		this.audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		this.flashlightHelper = flashlightHelper;
		this.zoomHelper = zoomHelper;
		this.filePath = filePath;
		
		surfaceView.getHolder().addCallback(this);

		/*
		 * deprecated setting, but required on Android versions prior to 3.0
		 * source: http://developer.android.com/guide/topics/media/camera.html
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		orientationListener = new AllianceOrientationEventListener(ctx, SensorManager.SENSOR_DELAY_NORMAL);
		orientationListener.addOrientationChangedListeners(this);
	}

	// SurfaceHolder.Callback ////////////////////////////////

	/**
	 * Called after onResume()
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("#", "surfaceCreated()");
		initCamera(holder);
		initCameraPreferences();
		
		if(zoomHelper != null) {
			zoomHelper.initZoom(parameters);
		}

		orientationListener.setCameraId(cameraId);
		orientationListener.enable();
		
		if (sensorAutoFocus != null) {
			sensorAutoFocus.setCamera(camera); // vielleicht raus?
			sensorAutoFocus.startAutoFocus();
		} else {
			MyFocusRectangle mFocusRectangle = (MyFocusRectangle) ((Activity)ctx).findViewById(R.id.focus_rectangle);
			sensorAutoFocus = new SensorAutoFocus(camera, mFocusRectangle, ctx);
			sensorAutoFocus.startAutoFocus();
		}
		
		if(allianceCameraListener != null) {
			allianceCameraListener.onCameraCreated();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d("#", "surfaceChanged(format=" + format + ", width=" + width + ", height=" + height + ")");
		// do nothing
	}

	/**
	 * Called after onPause()
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("#", "surfaceDestroyed()");

		sensorAutoFocus.stopAutoFocus();
		orientationListener.disable();
		camRelease();
	}

	// IAllianceOrientationChanged /////////////////////////

	@Override
	public void onAllianceOrientationChanged(int orientation, int orientationType, int rotation) {
		Log.d("#", "onAllianceOrientationChanged()");

		Parameters localParameters = camera.getParameters();
		/*
		 * Sets the rotation angle in degrees relative to the orientation of the
		 * camera. This affects the pictures returned from JPEG
		 * android.hardware.Camera.PictureCallback.
		 */
		localParameters.setRotation(rotation);
		camera.setParameters(localParameters);
	}

	public void addOrientationChangedListeners(IAllianceOrientationChanged listener) {
		orientationListener.addOrientationChangedListeners(listener);
	}

	// remaining methods ///////////////////////////////////////////////////

	/**
	 * 
	 * @param holder
	 */
	private void initCamera(SurfaceHolder holder) {

		try {

			// Dieser Code wird unten ersetzt
			camera = openCamera(cameraFacing);
			if (camera == null && useAlternativeFacing) {
				switch (cameraFacing) {
				case CameraInfo.CAMERA_FACING_BACK:
					camera = openCamera(cameraInfo.CAMERA_FACING_FRONT);
					break;
				case CameraInfo.CAMERA_FACING_FRONT:
					camera = openCamera(cameraInfo.CAMERA_FACING_BACK);
					break;
				}
			}

			if (camera != null) {
				camera.setPreviewDisplay(holder);

			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			camRelease();

			Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
			Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
		orientationListener.setCameraId(cameraId);
	}

	/**
	 * Opens a camera for the desired facing
	 * 
	 * @param desiredFacing
	 * @return initialized camera or null
	 */
	private Camera openCamera(int desiredFacing) {
		Camera cam = null;

		/*
		 * Oh no, more bugs!
		 * 
		 * Hier mal in deutsch Alex :-)
		 * 
		 * Wenn ich die Back-Kamera über den Code im Else-Block öffne sich die
		 * Preview öffnet und ich dann das Projekt im PreviewModus nochmals
		 * deploye, dann schmiert die Kamera ab, es wird eine Exception geworfen
		 * und camRelease() ausgeführt. Da die Kamera allerdings null ist, kann
		 * nichts released werden und ich muß mein Telefon neustarten, um die
		 * Kamera überhaupt nochmal nutzen zu können. Ohne Neustart geht es
		 * nicht mehr.
		 * 
		 * Verwendet man bei der Back-Facing-Kamera die Methode Camera.open()
		 * und deployed das Projekt neu, dann schmiert nichts ab
		 */
		if (desiredFacing == cameraInfo.CAMERA_FACING_BACK) {
			/**
			 * Creates a new Camera object to access the first back-facing
			 * camera on the device. If the device does not have a back-facing
			 * camera, this returns null.
			 * 
			 * @see #open(int)
			 */
			cam = Camera.open();

		} else {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			int cameraCount = Camera.getNumberOfCameras();
			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == desiredFacing) {
					cam = Camera.open(camIdx);
					cameraId = camIdx;
					break;
				}
			}
		}

		return cam;
	}

	private void initCameraPreferences() {
		if (camera != null) {

			camera.stopPreview();

			parameters = camera.getParameters();

			parameters.setPictureFormat(ImageFormat.JPEG);

			Size optimalPreviewSize = CameraPreviewSizeHelper.getBestPreviewSize(surfaceView.getWidth(), surfaceView.getHeight(), parameters.getSupportedPreviewSizes(),
					CameraPreviewSizeHelper.ASPECT_TOLERANCE);

			if (optimalPreviewSize != null) {
				parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
			}

			if(flashlightHelper != null){
				parameters.setFlashMode(flashlightHelper.getFlashlightMode());	
			}
			
			// Init available resolution
			resolutionHelper.initSupportedScreenSizes(parameters.getSupportedPictureSizes());
			
			// Setting 3 megapixel size as default
			resolutionHelper.setMegaPixelSizeOnDefault(3000000);
			parameters.setPictureSize(resolutionHelper.selectedResolution.width, resolutionHelper.selectedResolution.height);
			
			camera.setParameters(parameters);
			camera.startPreview();
		}
	}
	
	public void camRelease() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben ? wieso speicher freigeben
			camera = null;
		}

		if(sensorAutoFocus != null){
			sensorAutoFocus.setCamera(null);	
		}
		
	}

	/**
	 * Captures the image. TODO: If the camera is focusing nothing happens. If
	 * the last focus is more than ~10 seconds left, focus is triggered
	 */
	public void capture() {

		setSelectedPictureSize();
		
		// Turn Camera capture-sound mute
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		
		camera.takePicture(null, null, new PhotoCallback());
		
		// Turn Camera capture-sound normal
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	}

	private void setSelectedPictureSize(){
		parameters.setPictureSize(resolutionHelper.selectedResolution.width, resolutionHelper.selectedResolution.height);
		camera.setParameters(parameters);
	}
	
	private class PhotoCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera cam) {
			Log.d("#", "onPictureTaken()");

			// TODO: gibt es eine Device das orientation in den exif-daten
			// speichert? Ja das Samsung Galaxy 10.1n
			/*
			 * The camera driver may set orientation in the EXIF header without
			 * rotating the picture. Or the driver may rotate the picture and
			 * the EXIF thumbnail. If the Jpeg picture is rotated, the
			 * orientation in the EXIF header will be missing or 1 (row #0 is
			 * top and column #0 is left side).
			 */
			int orientation = Exif.getOrientation(data);
			Log.d("#", "onPictureTaken().orientation = " + orientation);

			try {

				filePath.mkdirs();

				FileOutputStream localFileOutputStream = new FileOutputStream(filePath);

				localFileOutputStream.write(data);
				localFileOutputStream.flush();
				localFileOutputStream.close();

				camera.startPreview();

			} catch (IOException localIOException) {
				// TODO
			}
		}

	}

	public Parameters getCameraParameters() {
		return camera.getParameters();
	}

	public void setCameraParameters(Parameters param) {
		camera.setParameters(param);
	}

	public void addAllianceCameraListener(IAllianceCameraListener allianceCameraListener) {
		this.allianceCameraListener = allianceCameraListener;
	}
}
