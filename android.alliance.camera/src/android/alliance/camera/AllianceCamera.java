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
	
	private String[] isoValues = {"auto"};
	private String isoValue;
	
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
			Toast.makeText(ctx, "Die Kamera ist nicht verf�gbar", Toast.LENGTH_LONG);
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
		 * Wenn ich die Back-Kamera �ber den Code im Else-Block �ffne sich die
		 * Preview �ffnet und ich dann das Projekt im PreviewModus nochmals
		 * deploye, dann schmiert die Kamera ab, es wird eine Exception geworfen
		 * und camRelease() ausgef�hrt. Da die Kamera allerdings null ist, kann
		 * nichts released werden und ich mu� mein Telefon neustarten, um die
		 * Kamera �berhaupt nochmal nutzen zu k�nnen. Ohne Neustart geht es
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
			
			// e.g.: auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600
			String values = parameters.get("iso-values");
			if(values != null) {
				isoValues = values.split(",");
			}
			isoValue = parameters.get("iso");
//			parameters.set("iso", "auto");
			
			// sharpness-max=30;zoom=0;scene-detect-values=off,on;zoom-supported=true;strtextures=OFF;sharpness-min=0;face-detection-values=;sharpness=10;contrast=5;whitebalance=auto;max-sharpness=30;scene-mode=auto;jpeg-quality=85;preview-format-values=yuv420sp;overlay-3d-format=0;histogram-values=enable,disable;jpeg-thumbnail-quality=70;preview-format=yuv420sp;overlay-format=33;face-detection=off;skinToneEnhancement=disable;preview-size=640x480;focal-length=4.31;auto-exposure-values=frame-average,center-weighted,spot-metering;continuous-af=caf-off;video-zoom-support=false;iso=auto;meter-mode=meter-center;record-size=;front-camera-mode=mirror;flash-mode-values=off,auto,on,torch;preview-frame-rate-values=5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31;preview-frame-rate=31;focus-mode-values=auto,infinity,normal,macro;jpeg-thumbnail-width=512;scene-mode-values=auto,landscape,snow,beach,sunset,night,portrait,backlight,sports,steadyphoto,flowers,candlelight,fireworks,party,night-portrait,theatre,action;preview-fps-range-values=(9000,30000);auto-exposure=frame-average;jpeg-thumbnail-size-values=512x288,480x288,432x288,512x384,352x288,0x0;histogram=disable;zoom-ratios=100;saturation-def=5;preview-size-values=1280x720,960x544,800x480,640x480,480x320;front_cam_sense30=1;smart-contrast=off;picture-size-values=3264x2448,3264x1952,3264x1840,2592x1952,2592x1936,2592x1728,2592x1552,2592x1456,2048x1536,2048x1360,2048x1216,2048x1152,1600x1200,1584x1056,1280x960,1280x848,1280x768,1280x720,1024x768,640x480,640x416,640x384,640x368,512x384,272x272;contrast-min=0;touch-af-aec=touch-off;preview-fps-range=9000,30000;min-exposure-compensation=-12;brightness-min=0;antibanding=off;taking-picture-zoom-min=0;saturation-min=1;contrast-max=10;vertical-view-angle=42.5;taking-picture-zoom-max=40;luma-adaptation=3;contrast-def=5;brightness-max=6;horizontal-view-angle=54.8;flip-video=-1;skinToneEnhancement-values=enable,disable;brightness=3;jpeg-thumbnail-height=384;cam-mode=0;focus-mode=auto;max-saturation=10;sharpness-def=10;max-contrast=10;preview-frame-rate-modes=frame-rate-auto,frame-rate-fixed;video-frame-format=yuv420sp;front-camera-mode-values=mirror,reverse,portrait-reverse;picture-format-values=jpeg,raw;saturation-max=10;max-exposure-compensation=12;exposure-compensation=0;exposure-compensation-step=0.166667;continuous-af-values=caf-off,caf-on;scene-detect=off;flash-mode=off;effect-values=none,mono,negative,solarize,sepia,posterize,whiteboard,aqua;picture-size=640x480;max-zoom=0;effect=none;3d-file-format=jps;saturation=5;whitebalance-values=auto,incandescent,fluorescent,daylight,cloudy-daylight;picture-format=jpeg;focus-distances=0.78,1.57,Infinity;lensshade-values=enable,disable;selectable-zone-af=auto;brightness-def=3;iso-values=auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600;selectable-zone-af-values=auto,spot-metering,center-weighted,frame-average;lensshade=enable;antibanding-values=off,50hz,60hz,auto
			String flatten = parameters.flatten();

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
			 *  Die beiden folgenden Aufrufe dieser beiden If-Anweisungen m�ssen an dieser Stelle
			 *  gesetzt werden, da sonst die Zoom-Buttons zu sp�t generiert werden und sie erscheinen
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
	
	public String[] getIsoValues() {
		return isoValues;
	}
	
	public void setIsoValue(String isoValue) {
		this.isoValue = isoValue;
		
		Parameters parameters = camera.getParameters();
		parameters.set("iso", isoValue);
		camera.setParameters(parameters);
	}
	
	public String getIsoValue() {
		return isoValue;
	}
}
