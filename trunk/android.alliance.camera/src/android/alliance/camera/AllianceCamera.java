package android.alliance.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import alliance.camera.R;
import android.alliance.helper.AllianceLocationListener;
import android.alliance.helper.AutoFocusHelper;
import android.alliance.helper.AutoFocusMode;
import android.alliance.helper.CameraPreviewSizeHelper;
import android.alliance.helper.CameraUtil;
import android.alliance.helper.Exif;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.ResolutionHelper;
import android.alliance.helper.ZoomHelper;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
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
	
	private AudioManager audioManager;
	
	private ResolutionHelper resolutionHelper =  ResolutionHelper.getInstance();
	public FlashlightHelper flashlightHelper;
	public AutoFocusHelper autofocusHelper;
	public ZoomHelper zoomHelper;
	private File filePath;
	private boolean closeAfterShot = false;
	
	/** e.g.: auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600 */
	private String[] isoValues = {"auto"};
	private String isoValue;
	/** e.g.: auto,incandescent,fluorescent,daylight,cloudy-daylight */
	private String[] whiteBalanceValues = {"auto"};
	private String whiteBalance;
	
	private LocationManager locManager;
	private LocationListener locListener;
	private boolean gps = true;
	
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
		
		if(camera != null){
			
			initCameraPreferences();
			autofocusHelper.initAutoFocus(camera);
			
			orientationListener.setCameraId(cameraId);
			orientationListener.enable();
			
			if(gps){
				initLocationManager();	
			}	
		
		} else {
			Toast.makeText(ctx, ctx.getResources().getString(R.string.cameraNotAvailable), Toast.LENGTH_LONG).show();
			((Activity) ctx).finish();
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

		autofocusHelper.stopAutoFocus();
		
		orientationListener.disable();
		releaseCamera();
	}

	// IAllianceOrientationChanged /////////////////////////

	@Override
	public void onAllianceOrientationChanged(int orientation, int orientationType, int rotation) {
		Log.d("#", "onAllianceOrientationChanged()");

		try{
			Parameters localParameters = camera.getParameters();
			/*
			 * Sets the rotation angle in degrees relative to the orientation of the
			 * camera. This affects the pictures returned from JPEG
			 * android.hardware.Camera.PictureCallback.
			 */
			localParameters.setRotation(frontFacingRotationFix(rotation));
			camera.setParameters(localParameters);	
		} catch(Exception e){
			Toast.makeText(ctx, ctx.getResources().getString(R.string.errorOrientationChange), Toast.LENGTH_LONG).show();
		}
	}

	public void addOrientationChangedListeners(IAllianceOrientationChanged listener) {
		orientationListener.addOrientationChangedListeners(listener);
	}
	
	// Fix the Front-Camera Rotation. When frontcamera is 90 or 270 degree, 
	// the system will be rotate false
	public int frontFacingRotationFix(int rotation){
		
		if(cameraFacing == CameraInfo.CAMERA_FACING_FRONT){
			switch(rotation){
			case 90:
			case 270:
				return (rotation + 180) % 360;
			}
		}
		
		return rotation;
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
					camera = openCamera(CameraInfo.CAMERA_FACING_FRONT);
					cameraFacing = CameraInfo.CAMERA_FACING_FRONT;
					break;
				case CameraInfo.CAMERA_FACING_FRONT:
					camera = openCamera(CameraInfo.CAMERA_FACING_BACK);
					cameraFacing = CameraInfo.CAMERA_FACING_BACK;
					break;
				}
			}

			if (camera != null) {
				
				camera.setPreviewDisplay(holder);
				initCameraDisplayOrientation(camera, cameraId, cameraFacing);
				
			} else {
				throw new Exception();
			}

			orientationListener.setCameraId(cameraId);
			
		} catch (Exception e) {
			releaseCamera();

			Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
			Toast.makeText(ctx, ctx.getResources().getString(R.string.cameraNotAvailable), Toast.LENGTH_LONG).show();
			((Activity) ctx).finish();
		}
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
	         result = (360 - result) % 360;  // compensate the mirror  TODO: Für was diese Zeile?
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

		if (desiredFacing == CameraInfo.CAMERA_FACING_BACK) {
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
		try{
			if (camera != null) {

				camera.stopPreview();

				Parameters parameters = camera.getParameters();
				
				// HTC Sensation: sharpness-max=30;zoom=0;scene-detect-values=off,on;zoom-supported=true;strtextures=OFF;sharpness-min=0;face-detection-values=;sharpness=10;contrast=5;whitebalance=auto;max-sharpness=30;scene-mode=auto;jpeg-quality=85;preview-format-values=yuv420sp;overlay-3d-format=0;histogram-values=enable,disable;jpeg-thumbnail-quality=70;preview-format=yuv420sp;overlay-format=33;face-detection=off;skinToneEnhancement=disable;preview-size=640x480;focal-length=4.31;auto-exposure-values=frame-average,center-weighted,spot-metering;continuous-af=caf-off;video-zoom-support=false;iso=auto;meter-mode=meter-center;record-size=;front-camera-mode=mirror;flash-mode-values=off,auto,on,torch;preview-frame-rate-values=5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31;preview-frame-rate=31;focus-mode-values=auto,infinity,normal,macro;jpeg-thumbnail-width=512;scene-mode-values=auto,landscape,snow,beach,sunset,night,portrait,backlight,sports,steadyphoto,flowers,candlelight,fireworks,party,night-portrait,theatre,action;preview-fps-range-values=(9000,30000);auto-exposure=frame-average;jpeg-thumbnail-size-values=512x288,480x288,432x288,512x384,352x288,0x0;histogram=disable;zoom-ratios=100;saturation-def=5;preview-size-values=1280x720,960x544,800x480,640x480,480x320;front_cam_sense30=1;smart-contrast=off;picture-size-values=3264x2448,3264x1952,3264x1840,2592x1952,2592x1936,2592x1728,2592x1552,2592x1456,2048x1536,2048x1360,2048x1216,2048x1152,1600x1200,1584x1056,1280x960,1280x848,1280x768,1280x720,1024x768,640x480,640x416,640x384,640x368,512x384,272x272;contrast-min=0;touch-af-aec=touch-off;preview-fps-range=9000,30000;min-exposure-compensation=-12;brightness-min=0;antibanding=off;taking-picture-zoom-min=0;saturation-min=1;contrast-max=10;vertical-view-angle=42.5;taking-picture-zoom-max=40;luma-adaptation=3;contrast-def=5;brightness-max=6;horizontal-view-angle=54.8;flip-video=-1;skinToneEnhancement-values=enable,disable;brightness=3;jpeg-thumbnail-height=384;cam-mode=0;focus-mode=auto;max-saturation=10;sharpness-def=10;max-contrast=10;preview-frame-rate-modes=frame-rate-auto,frame-rate-fixed;video-frame-format=yuv420sp;front-camera-mode-values=mirror,reverse,portrait-reverse;picture-format-values=jpeg,raw;saturation-max=10;max-exposure-compensation=12;exposure-compensation=0;exposure-compensation-step=0.166667;continuous-af-values=caf-off,caf-on;scene-detect=off;flash-mode=off;effect-values=none,mono,negative,solarize,sepia,posterize,whiteboard,aqua;picture-size=640x480;max-zoom=0;effect=none;3d-file-format=jps;saturation=5;whitebalance-values=auto,incandescent,fluorescent,daylight,cloudy-daylight;picture-format=jpeg;focus-distances=0.78,1.57,Infinity;lensshade-values=enable,disable;selectable-zone-af=auto;brightness-def=3;iso-values=auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600;selectable-zone-af-values=auto,spot-metering,center-weighted,frame-average;lensshade=enable;antibanding-values=off,50hz,60hz,auto
				// HTC Desire HD: sharpness-max=30;zoom=0;taking-picture-zoom=0;scene-detect-values=off,on;zoom-supported=true;strtextures=OFF;sharpness-min=0;sharpness=10;contrast=5;whitebalance=auto;scene-mode=auto;jpeg-quality=100;ola-fd-rect=;preview-format-values=yuv420sp;jpeg-thumbnail-quality=75;preview-format=yuv420sp;preview-size=640x480;focal-length=4.57;iso=auto;meter-mode=meter-center;flash-mode-values=off,auto,on,torch;preview-frame-rate-values=15;preview-frame-rate=15;focus-mode-values=auto,infinity;jpeg-thumbnail-width=640;scene-mode-values=auto,landscape,snow,beach,sunset,night,portrait,backlight,sports,steadyphoto,flowers,candlelight,fireworks,party,night-portrait,theatre,action;preview-fps-range-values=(1,200000);jpeg-thumbnail-size-values=640x480,512x384,384x288,0x0,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480,640x480;zoom-ratios=100,114,131,151,174,200;saturation-def=5;preview-size-values=1280x720,800x480,640x480,640x384;smart-contrast=off;picture-size-values=3264x2448,3264x1952,2592x1952,2592x1936,2592x1728,2592x1552,2048x1536,2048x1360,2048x1216,1600x1200,1584x1056,1280x960,1280x848,1280x768,1024x768,640x480,640x416,640x384,512x384,400x400,272x272;contrast-min=0;preview-fps-range=1,200000;min-exposure-compensation=-4;brightness-min=0;antibanding=auto;taking-picture-zoom-min=0;saturation-min=1;contrast-max=10;vertical-view-angle=40.74;taking-picture-zoom-max=40;contrast-def=5;brightness-max=6;horizontal-view-angle=52.68;brightness=3;jpeg-thumbnail-height=480;cam-mode=0;focus-mode=auto;sharpness-def=10;postview-size=640x480;video-frame-format=yuv420sp;picture-format-values=jpeg;saturation-max=10;max-exposure-compensation=4;exposure-compensation=0;exposure-compensation-step=0.5;scene-detect=off;flash-mode=off;effect-values=none,mono,negative,solarize,sepia,posterize,aqua;meter-mode-values=meter-average,meter-center,meter-spot;picture-size=3264x2448;max-zoom=5;effect=none;saturation=5;whitebalance-values=auto,incandescent,fluorescent,daylight,cloudy-daylight;picture-format=jpeg;focus-distances=0.78,1.57,Infinity;brightness-def=3;iso-values=auto,deblur,100,200,400,800,1250;antibanding-values=off,50hz,60hz,auto
//				String flatten = parameters.flatten();

				// e.g.: auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600
				String values = parameters.get("iso-values");
				if(values != null) {
					isoValues = values.split(",");
				}
				isoValue = parameters.get("iso");

				values = parameters.get("whitebalance-values");
				if(values != null) {
					whiteBalanceValues = values.split(",");
				}
				whiteBalance = parameters.getWhiteBalance();

				
				parameters.setPictureFormat(ImageFormat.JPEG);

				Size optimalPreviewSize = CameraPreviewSizeHelper.getBestPreviewSize(surfaceView.getWidth(), surfaceView.getHeight(), parameters.getSupportedPreviewSizes(),
						CameraPreviewSizeHelper.ASPECT_TOLERANCE);

				if (optimalPreviewSize != null) {
					parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
				}

				if(flashlightHelper != null && flashlightHelper.available){
					parameters.setFlashMode(flashlightHelper.flashStatus.flashMode);	
				}
				
				// Init available resolution
				resolutionHelper.initSupportedScreenSizes(parameters.getSupportedPictureSizes());
				resolutionHelper.calculateInitialSize();
				
				parameters.setPictureSize(resolutionHelper.selectedResolution.size.width, resolutionHelper.selectedResolution.size.height);
				
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
			
		} catch(Exception e){
			Toast.makeText(ctx, ctx.getResources().getString(R.string.errorLoadCameraPreview), Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	public void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben ? wieso speicher freigeben
			camera = null;
		}

		if(autofocusHelper != null){
			autofocusHelper.stopAutoFocus();	
		}
		
		if(audioManager != null) {
			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		}
		
		removeLocationManager();
	}

//	public void stopAutoFocus() {
//		if(autofocusHelper.available && sensorAutoFocus != null){
//			sensorAutoFocus.clearFocusView();
//			sensorAutoFocus.stopAutoFocus();
//			sensorAutoFocus.setCamera(null);
//			sensorAutoFocus = null;
//		}
//	}
	
	/**
	 * Captures the image. TODO: If the camera is focusing nothing happens. If
	 * the last focus is more than ~10 seconds left, focus is triggered
	 */
	public void capture() {
		
		try {
			if(autofocusHelper.available && autofocusHelper.autoFocusMode != AutoFocusMode.OFF) {
				if(autofocusHelper.autoFocus.isFocusing()) {
					return;
				} else {
					autofocusHelper.stopAutoFocus();
				}
			} 
			
			setSelectedPictureSize();
			
			camera.takePicture(null, null, new PhotoCallback());
			
			// Turn Camera capture-sound normal
	
		} catch(Exception e){
			Toast.makeText(ctx, ctx.getResources().getString(R.string.errorShotPhoto), Toast.LENGTH_LONG).show();
		}
	}
	
	public void capture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg) {
		
		try{
			if(autofocusHelper.autoFocusMode != AutoFocusMode.OFF) {
				if(autofocusHelper.isFocusing()) {
					return;
				} else {
					autofocusHelper.stopAutoFocus();
				}
			}
			
			setSelectedPictureSize();
			
			camera.takePicture(shutter, raw, jpeg);
			
		} catch(Exception e){
			Toast.makeText(ctx, ctx.getResources().getString(R.string.errorShotPhoto), Toast.LENGTH_LONG).show();
		}
	}

	private void setSelectedPictureSize(){
		Parameters parameters = camera.getParameters();
		parameters.setPictureSize(resolutionHelper.selectedResolution.size.width, resolutionHelper.selectedResolution.size.height);
		camera.setParameters(parameters);
	}
	
	
	/*
	 * ResolutionHelper is a singleton. Set the other values 0 
	 */
	public void setPictureSizeMegapixel(int megapixel) {
		resolutionHelper.initalSizeMegapixel = megapixel;
		resolutionHelper.initialSizeHeight = 0;
		resolutionHelper.initalSizeWidth = 0;
	}
	
	public void setPictureSize(int width, int height){
		resolutionHelper.initialSizeHeight = height;
		resolutionHelper.initalSizeWidth = width;
		resolutionHelper.initalSizeMegapixel = 0;
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
			 * 
			 * 
			 * 3, 6, 8 
			 * 
			 */
			
			boolean doSaveWithoutRotation = true;
			
			int orientation = Exif.getOrientation(data);
			Log.d("#", "onPictureTaken().orientation = " + orientation);
			
			orientation = frontFacingRotationFix(orientation);
			
			if(orientation != 0) {

				Bitmap bmpSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
					
				if(bmpSrc.getWidth()*bmpSrc.getHeight() > 4000000) {
					doSaveWithoutRotation = true;
					
				} else {
					
					doSaveWithoutRotation = false;
					
					Bitmap bmpRotated = CameraUtil.rotate(bmpSrc, orientation);
					bmpSrc.recycle();

					try {
						
						FileOutputStream localFileOutputStream = new FileOutputStream(filePath);
						bmpRotated.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
						
						localFileOutputStream.flush();
						localFileOutputStream.close();
						bmpRotated.recycle();
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} 
			
			if(doSaveWithoutRotation){

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
			
			onAfterPhotoTaken();
		}
	}
	
	public void onAfterPhotoTaken() {
		
		// Set the Activities Result
		if(allianceCameraListener != null) {
			allianceCameraListener.afterPhotoTaken();
		}
		
		if(closeAfterShot){
			((Activity) ctx).finish();
		} else {
			camera.startPreview();
			if(autofocusHelper.available && autofocusHelper.autoFocusMode != AutoFocusMode.OFF) {
				autofocusHelper.startAutoFocus();
			}
		}
	}

	public Parameters getCameraParameters() {
		return camera.getParameters();
	}

	public Camera getCamera(){
		return camera;
	}
	
	public void setCameraParameters(Parameters param) {
		camera.setParameters(param);
		
	}

	public void addAllianceCameraListener(IAllianceCameraListener allianceCameraListener) {
		this.allianceCameraListener = allianceCameraListener;
	}

	/**
	 * If photo is taken. Should the camera-activity to be close?
	 * Default is false
	 */
	public void setInitCloseAfterShut(boolean value){
		this.closeAfterShot = value;
	}
	
	/**
	 * Init if flashlight should be available and set the first element of sequence als default
	 * 
	 * If value -1 then take first element on list. Other take element at values position
	 */
	public void setInitFlashlightHelper(FlashlightHelper flashlightHelper, int value){
		this.flashlightHelper = flashlightHelper;
		this.flashlightHelper.flashStatus = flashlightHelper.sequence.get(value == -1 ? 0 : value);
	}
	
	public void setAutoFocusHelper(AutoFocusHelper autofocusHelper){
		this.autofocusHelper = autofocusHelper;
	}
	
	public void nextFlashMode(ImageView iv) {
		Parameters param = camera.getParameters();
		flashlightHelper.next(param, iv);
		camera.setParameters(param);
	}

	/**
	 * Init if zoom should be available
	 */
	public void setInitZoomHelper(ZoomHelper zoomHelper){
		this.zoomHelper = zoomHelper;
	}
	
	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * e.g.: auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600
	 * @return
	 */
	public String[] getIsoValues() {
		return isoValues;
	}
	
	public void setIso(String iso) {
		this.isoValue = iso;
		
		Parameters parameters = camera.getParameters();
		parameters.set("iso", iso);
		camera.setParameters(parameters);
	}
	
	public String getIsoValue() {
		return isoValue;
	}
	
	private void initLocationManager(){
		
		locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        
		long gpsBoostDistanz = 0;
		long gpsBoostZeit = 1000;
		
		removeLocationManager();
		
		locListener = new AllianceLocationListener(camera);
		
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, gpsBoostZeit, gpsBoostDistanz, locListener);
	}

	private void removeLocationManager() {
		if(locManager != null && locListener != null){
			locManager.removeUpdates(locListener);	
		}
	}
	
	public String[] getWhiteBalanceValues() {
		return whiteBalanceValues;
	}
	
	public void setWhiteBalance(String whiteBalance) {
		this.whiteBalance = whiteBalance;
		
		Parameters parameters = camera.getParameters();
		parameters.setWhiteBalance(whiteBalance);
		camera.setParameters(parameters);
	}
	
	public String getWhiteBalance() {
		return whiteBalance;
	}
	
	public void setGps(boolean value){
		this.gps = value;
	}
}
