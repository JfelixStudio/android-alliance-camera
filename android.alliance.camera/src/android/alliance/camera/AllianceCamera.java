package android.alliance.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.alliance.camera.CameraHelper.CameraTarget;
import android.alliance.data.VOContextMenu;
import android.alliance.focus.IntervalAutoFocus;
import android.alliance.focus.MyFocusRectangle;
import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.WidgetScaler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.GestureDetector;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

public class AllianceCamera extends Activity implements Callback {

	public Camera camera;
	private WindowManager wm = null;
	private OrientationEventListener orientationListener;
	private Orientation orientation = Orientation.getInstance();
	private List<Size> lSupportedPictureSizes = null;
	private AudioManager audioManager;
	private android.alliance.camera.TextViewRotate txAufloesung;
	private static Size preSelection = null;
	private Display display = null;
	private WidgetScaler ws = null;
	private ImageButton btTakePhoto;
	private float lastRotation = 0.0f;
	
	private CameraHelper cameraHelper = CameraHelper.getInstance();
	private CameraTarget targetAufloesung = CameraTarget.AUFLOESUNG;
	private SurfaceView sv;
	private ImageButton btFlashlight;
	
	int currentZoomLevel = 0; 
	int maxZoomLevel = 0;
	private ImageButton zoomIn;
	private ImageButton zoomOut;
	private Bitmap bmpFlashlight = null;
	private Bitmap bmpZoomIn = null;
	private Bitmap bmpShutter = null;
	private Bitmap bmpZoomOut = null;
	
    private MyFocusRectangle mFocusRectangle;
    private GestureDetector mGestureDetector;
		
	
	private Parameters parameters = null;
	private boolean mSmoothZoomSupported;
	
	private SensorManager mySensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagnetometer;

	
    float[] mGravity;
    float[] mGeomagnetic;
    float[] mOrientation;
	
    float[] mGravityOnLastFocus;
    float[] mGeomagneticOnLastFocus;
    float[] mOrientationOnLastFocus;
	private Bitmap bmpAufloesung;
    
    private IntervalAutoFocus intervalAutoFocus;
    private SensorAutoFocus sensorAutoFocus;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Mu� aufgerufen werden, bevor Inhalte der Kamera zugewiesen werden
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.alliancecamera);
		
		ws = WidgetScaler.getInstance(this);
		
		wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		 
		bmpFlashlight = BitmapFactory.decodeResource(this.getResources(), R.drawable.bt_flashlight);
		bmpZoomIn = BitmapFactory.decodeResource(this.getResources(), R.drawable.bt_zoomin);
		bmpZoomOut = BitmapFactory.decodeResource(this.getResources(), R.drawable.bt_zoomout);
		bmpShutter = BitmapFactory.decodeResource(this.getResources(), R.drawable.camshutter);
		bmpAufloesung = BitmapFactory.decodeResource(this.getResources(), R.drawable.bt_aufloesung);
		
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		sv = (SurfaceView) findViewById(R.id.sv_camera);
		sv.getHolder().addCallback(this);
		sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// Wird aufgerufen, wenn man von Portrait zu Landscape wechselt.
		this.orientationListener = new OrientationEventListener(this, 1){
	  	    public void onOrientationChanged(int paramAnonymousInt){
	  	    	// Hier wird die Orientation geupdated
	  	  		
	  	    	int angle = orientation.getAngle();
	  	    	
	  	    	orientation.update(paramAnonymousInt);	  
	  	  		
	  	    	if(angle != orientation.getAngle()){
	  	    		if(btTakePhoto != null){
	  	    			btTakePhoto.setLayoutParams(ws.get_camera_shutterbutton_layout());
	  	    			btTakePhoto.setBackgroundDrawable(rotateBitmap(bmpShutter, btTakePhoto));
		  	  		}	
	  	    		
	  	    		if(txAufloesung != null){
	  	    	        txAufloesung.setLayoutParams(ws.get_camera_aufloesung_layout());
	  	    	        txAufloesung.setBackgroundDrawable(rotateBitmap(bmpAufloesung, txAufloesung));
	  	    		}
	  	    		
	  	    		if(btFlashlight != null) {
	  	    			btFlashlight.setLayoutParams(ws.get_camera_flashlight_layout());
	  	    			btFlashlight.setBackgroundDrawable(rotateBitmap(bmpFlashlight, btFlashlight));
						    
	  	    		}
	  	    		
	  	    		if(zoomIn != null){
	  	    			zoomIn.setLayoutParams(ws.get_camera_zoom_in_layout());
	  	    			zoomIn.setBackgroundDrawable(rotateBitmap(bmpZoomIn, zoomIn));
	  	    		}
	  	    		
	  	    		if(zoomOut != null){
	  	    			zoomOut.setLayoutParams(ws.get_camera_zoom_out_layout());
	  	    			zoomOut.setBackgroundDrawable(rotateBitmap(bmpZoomOut, zoomOut));
	  	    		}
	  	    	}
	  	    }
	  	};
		
	  	txAufloesung = (android.alliance.camera.TextViewRotate) findViewById(R.id.aufloesung);
	  	txAufloesung.setLayoutParams(ws.get_camera_aufloesung_layout());
	  	txAufloesung.setBackgroundDrawable(rotateBitmap(bmpAufloesung, txAufloesung));
	  	txAufloesung.setOnClickListener(new OnClickListener(){

	    		@Override
	    		public void onClick(View v) {

	    			if(lSupportedPictureSizes != null){
	    				
	    				cameraHelper.clearContextMenuItems();
	    				
	    				int i = 0;
	    				for(Size s : lSupportedPictureSizes){
	    					if(s.width == preSelection.width && s.height == preSelection.height){
	    						cameraHelper.setSelectedContextMenuItem(new VOContextMenu(i, s.width + "x" + s.height));
	    					}
	    					
	    					cameraHelper.addContextMenuItem(new VOContextMenu(i, s.width + "x" + s.height));
	    					i++;
	    				}
	    				
	    				// Camera wird released. Ansonsten w�rde der Background des Dialogs gedreht erscheinen.
	    				camRelease();
	    				
	    				Intent x = new Intent(AllianceCamera.this, LayerActivity.class);
	    				x.putExtra(CameraHelper.CameraTarget.CAMERATARGET.getName(), targetAufloesung.getName());
	    				startActivityForResult(x, targetAufloesung.getId());
	    			}
	    		}
	        });	  	
	  	
	  	btTakePhoto = (ImageButton) findViewById(R.id.takepicture);
	  	btTakePhoto.setLayoutParams(ws.get_camera_shutterbutton_layout());
	  	btTakePhoto.setBackgroundDrawable(rotateBitmap(bmpShutter, btTakePhoto));
        btTakePhoto.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			
    			// Rotation wird aktualisiert
    			Camera.Parameters localParameters = camera.getParameters();
    			localParameters.setRotation(orientation.getAngle());
    			camera.setParameters(localParameters);
    				
    			// Stummschalten des Ausl�setons
    			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    			
    			camera.takePicture(null, null, new PhotoCallback());
    		}
        });
        
    	btFlashlight = (ImageButton) findViewById(R.id.flashlight);
		btFlashlight.setLayoutParams(ws.get_camera_flashlight_layout());
		btFlashlight.setBackgroundDrawable(rotateBitmap(bmpFlashlight, btFlashlight));
    	btFlashlight.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			
				boolean available = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
				
				if(available){
					 Parameters params = camera.getParameters();
	    	            if(params.getFlashMode().equals(Parameters.FLASH_MODE_OFF)){
	    	            	params.setFlashMode( Parameters.FLASH_MODE_TORCH);
	    	            } else if(params.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)){
	    	            	params.setFlashMode( Parameters.FLASH_MODE_OFF);
	    	            }
	    	            camera.setParameters(params);
	    	            camera.startPreview();
				}
    		}
        });
    	
    	zoomIn = (ImageButton) findViewById(R.id.zoomIn);
    	zoomIn.setLayoutParams(ws.get_camera_zoom_in_layout());
    	zoomIn.setBackgroundDrawable(rotateBitmap(bmpZoomIn, zoomIn));
    	zoomIn.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			currentZoomLevel++;
        		if(currentZoomLevel > maxZoomLevel){
        			currentZoomLevel = maxZoomLevel;
        		}
        		Parameters param = camera.getParameters();
        		param.setZoom(currentZoomLevel);
        		camera.setParameters(param);
    		}
    	});
    		
    	zoomOut = (ImageButton) findViewById(R.id.zoomOut);
    	zoomOut.setLayoutParams(ws.get_camera_zoom_out_layout());
    	zoomOut.setBackgroundDrawable(rotateBitmap(bmpZoomOut, zoomOut));
    	zoomOut.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			currentZoomLevel--;
        		if(currentZoomLevel < 0){
        			currentZoomLevel = 0;
        		}
        		
        		Parameters param = camera.getParameters();
        		param.setZoom(currentZoomLevel);
        		camera.setParameters(param);
    		}
    	});
    	
    	mFocusRectangle = (MyFocusRectangle) findViewById(R.id.focus_rectangle);
    	mFocusRectangle.setLayoutParams(ws.get_camera_fokus_layout());
    	
    	// Initialisieren der Sensoren f�r den Autofokus
    	mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    	
        sensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
      
//    	updateFocusIndicator();
	}

		 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//		doFocus(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		boolean cam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean camfront = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

		if(camera == null){
			
			try{
				// Wenn die normale Kamera nicht verf�gbar ist
				if(!cam) {
					
					// Pr�fe ob die Frontkamera verf�gbar ist
					if(camfront){
						
						// Hier werden die Kameras ausgelesen und die Frontkamera initialisiert
						Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
					    int cameraCount = Camera.getNumberOfCameras();
					    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
					        Camera.getCameraInfo( camIdx, cameraInfo );
					        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
					            try {
					                camera = Camera.open( camIdx );
					            } catch (RuntimeException e) {
					            }
					        }
					    }
					}
					
				} else {
					
					camera = Camera.open();
				}
				
			} catch(Exception e){
				camera.release();
	            camera = null;
			}
			
			
			try {
				camera.setPreviewDisplay(holder);
				
			} catch (Throwable t) {
				camera.release();
	            camera = null;
			}
		}
		
		parameters = camera.getParameters();
		
		Size size = getBestPreviewSize(display.getWidth(), display.getHeight(), parameters);
		
		if(size == null){
			size = getBestPreviewSize(display.getHeight(), display.getWidth(), parameters);
		}
		
		camera.stopPreview();
		
		
		parameters.setPictureFormat(ImageFormat.JPEG);

		
		if(size != null){
			parameters.setPreviewSize(size.width, size.height);
		}
		
		lSupportedPictureSizes = parameters.getSupportedPictureSizes();

		if(lSupportedPictureSizes != null){
			if(preSelection == null){
				preSelection = get3MegaPixelSize(lSupportedPictureSizes);	
			}

			txAufloesung.setText(preSelection.width + "x" + preSelection.height);
			
			parameters.setPictureSize(preSelection.width, preSelection.height);

		}
		
		initZoom(); 
		initFlashlight();
		
		List<String> focusModes = parameters.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		
		camera.setParameters(parameters);
		
		camera.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera arg1) {

                /**
                 * TODO: Anstatt den Autofokus alle 3 Sekunden neu zu machen k�nnte man die ausgelesenen Sensorenwerte nehmen:
                 * 
                 *  float[] mGravity;
				    float[] mGeomagnetic;
				    float[] mOrientation;
					
				    float[] mGravityOnLastFocus;
				    float[] mGeomagneticOnLastFocus;
				    float[] mOrientationOnLastFocus;
    
    			   und einen Toleranzbereich festlegen. Wenn sich das Device innerhalb des Toleranzbereichs befindet, wird der
    			   Autofokus nicht neu gesetzt. Wenn sich das Device au�erhalb des Bereichs befindet, wir der Autofokus neu gesetzt
    
                 */
            }
        });
		
		camera.startPreview();
		
//		intervalAutoFocus = new IntervalAutoFocus(camera, mFocusRectangle);
//		intervalAutoFocus.startAutoFocus();
		
		sensorAutoFocus = new SensorAutoFocus(camera, mFocusRectangle);
		sensorAutoFocus.startAutoFocus();
		
		mySensorManager.registerListener(sensorAutoFocus, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		mySensorManager.registerListener(sensorAutoFocus, sensorMagnetometer, SensorManager.SENSOR_DELAY_UI);
	}

	
	private void initZoom(){
		if(parameters.isZoomSupported()){    
    	    maxZoomLevel = parameters.getMaxZoom();
            mSmoothZoomSupported = parameters.isSmoothZoomSupported();
    	    currentZoomLevel = parameters.getZoom();
    	    
	   } else {
		   zoomIn.setVisibility(View.GONE);
		   zoomOut.setVisibility(View.GONE);
	   }
	}
	
	private void initFlashlight(){
		boolean available = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		if(!available){
			btFlashlight.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camRelease();
	}

	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}
	
	
	private Size get3MegaPixelSize(List<Size> sizes){
		
		if(sizes != null){
			List<Size> lx = new ArrayList<Size>();
			
			for(Size s : sizes){
				int result = s.width * s.height;
				
				if(result < 1500000){
					lx.add(s);
				}
			}
			
			return lx.get(0);
		}
		
		return null;
	}
	
	 public void updateCameraOrientation() {
	    parameters.setRotation(orientation.getAngle());
	    camera.setParameters(parameters);
	 }

	 private class PhotoCallback implements Camera.PictureCallback {
	
		 public void onPictureTaken(byte[] jpeg, Camera paramCamera) {
		  
			 audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
			 
			 try {
					 
			      String str = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";
			 
		    	  File localFile1 = new File(Environment.getExternalStorageDirectory(), "/CamTest/");
			      localFile1.mkdirs();
			      
			      File localFile2 = new File(localFile1, str);
				        
			      
			      FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
			      
			      localFileOutputStream.write(jpeg);
			      localFileOutputStream.flush();
			      localFileOutputStream.close();
			    
			      Toast.makeText(AllianceCamera.this, "Bild wurde gespeichert!", Toast.LENGTH_SHORT).show();
			      
			      startPreview();
			      
		    } catch (IOException localIOException) {
		    	Toast.makeText(AllianceCamera.this, "Fehler!", Toast.LENGTH_SHORT).show();
		    }		
		 }			 
	}



	private void camRelease(){
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben
			camera = null;
		}
	}

	
	@Override
	protected void onStop() {
		super.onStop();
		camRelease();
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		this.orientationListener.disable();
//		mySensorManager.unregisterListener(this);
		mySensorManager.unregisterListener(sensorAutoFocus);
		
//		intervalAutoFocus.stopAutoFocus();
		sensorAutoFocus.stopAutoFocus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.orientationListener.enable();
		
		if(sensorAutoFocus != null) {
			mySensorManager.registerListener(sensorAutoFocus, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
			mySensorManager.registerListener(sensorAutoFocus, sensorMagnetometer, SensorManager.SENSOR_DELAY_UI);
			sensorAutoFocus.startAutoFocus();
		}
//	    intervalAutoFocus.startAutoFocus();
		
	}

	public void startPreview(){
		if(camera != null){
			camera.startPreview();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Hier wird die selektierte Aufl�sung aus dem Dialog der preSelection Variable bzw. der TextView zugewiesen
		if(requestCode == targetAufloesung.getId()){
			
			preSelection = lSupportedPictureSizes.get(cameraHelper.getSelectedContextMenuItem().getId());
			txAufloesung.setText(preSelection.width + "x" + preSelection.height);
			
		}
		
		// Anschlie�end wird die Camera neu gestartet
		sv.getHolder().addCallback(this);
		sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	private Drawable rotateBitmap(Bitmap target, View widget){
		int angle = orientation.getAngle();
		
		// Mu� man nochmal erzeugne, da target immutable ist
		Bitmap bmResult = Bitmap.createBitmap(target.getWidth(), target.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmResult); 
		
		 if(angle == 270){
        	 canvas.rotate(90);	
        	 canvas.translate(0, -target.getWidth()); 
         
         } else if(angle == 180){
        	 canvas.rotate(180);
        	 canvas.translate(-target.getWidth(), -target.getHeight()); // kleiner = tiefer
        	 
         } else if(angle == 90){
        	
        	 canvas.rotate(-90);
        	 canvas.translate(-target.getHeight(), 0); // gr��er = tiefer
        	 
         } else if(angle == 0){
        	 // do nothing
         }
		
		 canvas.drawBitmap(target, 0, 0, null);

		 BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmResult);
		 
		 return (Drawable) bitmapDrawable;
	}
}
