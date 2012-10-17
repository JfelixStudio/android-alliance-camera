package android.alliance.camera;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
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
	
	public static String INTENT_KEY_USE_ALTERNATIVE_FACING = "UseAlternativeFacing";
	
	private Context ctx;
	private Camera camera;
	private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	private SurfaceView surfaceView;
	private Parameters parameters;
//	private Display display = null;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 
	 * AllianceCamera.CAMERA_FACING_BACK_OR_FRONT */
	private Integer cameraFacing = null;
	
	private boolean useAlternativeFacing = false;
	
	public AllianceCamera(Context ctx, SurfaceView surfaceView, int cameraFacing, boolean useAlternativeFacing) {
		this.ctx = ctx;
		this.surfaceView = surfaceView;
		this.cameraFacing = cameraFacing;
		this.useAlternativeFacing = useAlternativeFacing;
		
		surfaceView.getHolder().addCallback(this);

		/*
		 * deprecated setting, but required on Android versions prior to 3.0
		 * source: http://developer.android.com/guide/topics/media/camera.html
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
		
		/**
		 * 
		 * @param holder
		 */
		private void initCamera(SurfaceHolder holder) {
			
			try {
				
				// Dieser Code wird unten ersetzt
				camera = openCamera(cameraFacing);
				if(camera == null && useAlternativeFacing) {
					switch(cameraFacing) {
					case CameraInfo.CAMERA_FACING_BACK:
						camera = openCamera(cameraInfo.CAMERA_FACING_FRONT);
						break;
					case CameraInfo.CAMERA_FACING_FRONT:
						camera = openCamera(cameraInfo.CAMERA_FACING_BACK);
						break;
					}
				}
				
			
				if(camera != null){
					camera.setPreviewDisplay(holder);
					
				} else {
					throw new Exception();
				}
				
			} catch (Exception e) {
				camRelease();

				Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
				Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
			}
		}
		
		/**
		 * Opens a camera for the desired facing
		 * @param desiredFacing
		 * @return initialized camera or null
		 */
		private Camera openCamera(int desiredFacing) {
			Camera cam = null;
			
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			int cameraCount = Camera.getNumberOfCameras();
			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == desiredFacing) {
					cam = Camera.open(camIdx);
				}
			}
			return cam;
		}
		
		private void initCameraPreferences() {
			if (camera != null) {

				camera.stopPreview();

				parameters = camera.getParameters();

				parameters.setPictureFormat(ImageFormat.JPEG);

				// Setze BestPreviewSize
				// display.width is deprecated was f�r Alternativen gibt es? die View die als Zeichenfl�che dient
				int widthSurface = surfaceView.getWidth();
				int heightSurface = surfaceView.getHeight();
				
				Size optimalPreviewSize = getBestPreviewSize(widthSurface, heightSurface, parameters.getSupportedPreviewSizes());
				
				if(optimalPreviewSize != null){
					parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);	
				}
				
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
				camera.release(); // Speicher freigeben ? wieso speicher freigeben
				camera = null;
			}
		}
		
		/**
		 * good source: http://www.java2s.com/Code/Android/Hardware/Gettheoptimalpreviewsizeforthegivenscreensize.htm
		 * @param width
		 * @param height
		 * @param supportedPreviewSizes
		 * @return
		 */
		private Size getBestPreviewSize(int width, int height, List<Size> supportedPreviewSizes) {
			Double sourceRatio = null;

	        if(width < height){
                 sourceRatio = (double) width / height;
	         } else {
                 sourceRatio = (double) height / width;
	         }

	        int index = 0;
	        Double lastRatioToCheck = null;

	        for (Size size : supportedPreviewSizes) {

	        	Double targetRatio = null;
				
				if(size.width > size.height){
					sourceRatio = (double) size.width / size.height;
				} else {
					sourceRatio = (double) size.height / size.width;
				}
				
	            if(lastRatioToCheck == null){
                     lastRatioToCheck = targetRatio;
                     index = 0;

	            } else {
	            	// Because of Math.ab there gonves No negative ratio values
	                double chkLastTarget = Math.abs(lastRatioToCheck);
	                double chkCurrentTarget = Math.abs(targetRatio);

	                double chkSource = Math.abs(sourceRatio);

	                if(Math.abs(chkSource - chkCurrentTarget) < Math.abs(chkSource - chkLastTarget)){
	                	lastRatioToCheck = targetRatio;
	                    index = supportedPreviewSizes.indexOf(targetRatio);
	                 }
	             }
	        }

	        return supportedPreviewSizes.get(index);
		}	        
	        
	        
	        

}