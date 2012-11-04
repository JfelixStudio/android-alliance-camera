package android.alliance.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.alliance.focus.MyFocusRectangle;
import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.CameraPreviewSizeHelper;
import android.alliance.helper.Exif;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author alliance
 *
 */
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
	private IAllianceCameraListener allianceCameraListener;
	
	
	private AllianceOrientationEventListener orientationListener;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 <br>
	 * AllianceCamera.CAMERA_FACING_BACK_OR_FRONT
	 */
	private Integer cameraFacing = null;
	private boolean useAlternativeFacing = false;
	
	private SensorAutoFocus sensorAutoFocus;

	private AudioManager audioManager;
	
	private ResolutionHelper resolutionHelper =  ResolutionHelper.getInstance();
	public FlashlightHelper flashlightHelper;
	public ZoomHelper zoomHelper;
	private File filePath;
	private boolean closeAfterShot = false;
	private int initPictureSize = 3000000;
	
	public AllianceCamera(Context ctx, SurfaceView surfaceView, int cameraFacing, boolean useAlternativeFacing, File filePath) {
		Log.d("#", "AllianceCamera()");
		this.ctx = ctx;
		this.surfaceView = surfaceView;
		this.cameraFacing = cameraFacing;
		this.useAlternativeFacing = useAlternativeFacing;
		this.filePath = filePath;
		
		audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager != null){
			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);	
		}
		
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
		releaseCamera();
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
//		localParameters.setRotation(rotation);
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
				initCameraDisplayOrientation(camera, cameraId, cameraFacing);
				
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			releaseCamera();

			Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
			Toast.makeText(ctx, "Die Kamera ist nicht verfügbar", Toast.LENGTH_LONG);
			((Activity) ctx).finish();
		}
		orientationListener.setCameraId(cameraId);
	}
	
	/**
	 * If the Activity is not fixed to landscape this function is important.<br>
	 * 1. degrees = the rotation of the screen from its "natural" orientation. <br>
	 * 2. info.orientation = The orientation of the camera image. The value is the angle that the 
	 * camera image needs to be rotated clockwise so it shows correctly on the display in its natural orientation.
	 * @param camera
	 */
	private void initCameraDisplayOrientation(Camera camera, int cameraId, int camerFacing) {
		Camera.CameraInfo info = new Camera.CameraInfo();
	    Camera.getCameraInfo(cameraId, info);
	     
		WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
	    int rotation = winManager.getDefaultDisplay().getRotation();
	    int degrees = 0;

	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
		camera.setDisplayOrientation(result);
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

			Parameters parameters = camera.getParameters();

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
			resolutionHelper.setMegaPixelSizeOnDefault(initPictureSize);
			parameters.setPictureSize(resolutionHelper.selectedResolution.width, resolutionHelper.selectedResolution.height);
			
			camera.setParameters(parameters);
			
			/*
			 *  Die beiden folgenden Aufrufe dieser beiden If-Anweisungen müssen an dieser Stelle
			 *  gesetzt werden, da sonst die Zoom-Buttons zu spät generiert werden und sie erscheinen
			 *  nicht sofort auf dem Display
			 */
			if(zoomHelper != null) {
				zoomHelper.initZoom(parameters);
			}
			
			if(allianceCameraListener != null) {
				allianceCameraListener.onCameraCreated();
			}
			
			camera.startPreview();
		}
	}
	
	public void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben ? wieso speicher freigeben
			camera = null;
		}

		if(sensorAutoFocus != null){
			sensorAutoFocus.setCamera(null);	
		}
		
		if(audioManager != null) {
			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		}
	}

	/**
	 * Captures the image. TODO: If the camera is focusing nothing happens. If
	 * the last focus is more than ~10 seconds left, focus is triggered
	 */
	public void capture() {
		
		if(sensorAutoFocus.isFocusing()) {
			return;
		} else {
			sensorAutoFocus.stopAutoFocus();
		}

		setSelectedPictureSize();
		
		camera.takePicture(null, null, new PhotoCallback());
		
		// Turn Camera capture-sound normal

	}

	private void setSelectedPictureSize(){
		Parameters parameters = camera.getParameters();
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
			
			if(orientation != 0) {
				Bitmap bmpSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				if(bmpSrc.getWidth()*bmpSrc.getHeight() > 4000000) {
					Toast.makeText(ctx, "image to big", Toast.LENGTH_SHORT).show();
				}
				
				Bitmap bmpRotated = rotate(bmpSrc, orientation);
				bmpSrc.recycle();
				
				try {
					
					FileOutputStream localFileOutputStream = new FileOutputStream(filePath);
					bmpRotated.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
			} else {

				try {
	
					FileOutputStream localFileOutputStream = new FileOutputStream(filePath);
	
					localFileOutputStream.write(data);
					localFileOutputStream.flush();
					localFileOutputStream.close();
	
				} catch (IOException localIOException) {
					// TODO
					Log.e("#",localIOException.getMessage());
				}
			}
			
			allianceCameraListener.afterPhotoTaken();

			if(closeAfterShot){
				((Activity) ctx).finish();
			} else {
				camera.startPreview();
				sensorAutoFocus.startAutoFocus();
			}
		}

	}
	
	
	
	/**
	 * Rotates a bitmap by some degrees. The bmpSrc stays untouched and a new rotated
	 * Bitmap gets created.
	 * @param bmpSrc
	 * @param degrees	new rotated Bitmap
	 * @return
	 */
	public Bitmap rotate(Bitmap bmpSrc, int degrees) {
		int w = bmpSrc.getWidth();
		int h = bmpSrc.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(degrees);
		Bitmap bmpTrg = Bitmap.createBitmap(bmpSrc, 0, 0, w, h, mtx, true);
		return bmpTrg;
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
	

	/**
	 * The defalt, preselected picture size. 
	 * If not set, the default value is 3000000
	 */
	public void setInitPictureSize(int initPictureSize){
		this.initPictureSize = initPictureSize;
	}
	
	/**
	 * If photo is taken. Should the camera-activity to be close?
	 * Default is false
	 */
	public void setInitCloseAfterShut(boolean value){
		this.closeAfterShot = value;
	}
	
	/**
	 * Init if flashlight should be available
	 */
	public void setInitFlashlightHelper(FlashlightHelper flashlightHelper){
		this.flashlightHelper = flashlightHelper;
	}

	/**
	 * Init if zoom should be available
	 */
	public void setInitZoomHelper(ZoomHelper zoomHelper){
		this.zoomHelper = zoomHelper;
	}
	
	public void setFilePaht(File filePath) {
		this.filePath = filePath;
	}
}
