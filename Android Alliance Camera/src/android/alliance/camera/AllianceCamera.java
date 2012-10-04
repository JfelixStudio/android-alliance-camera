package android.alliance.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.alliance.camera.R;
import android.alliance.camera.CameraHelper.CameraTarget;
import android.alliance.data.VOContextMenu;
import android.alliance.dialoge.MySpinnerMenuDialog;
import android.alliance.helper.WidgetScaler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Mu� aufgerufen werden, bevor Inhalte der Kamera zugewiesen werden
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.alliancecamera);
		
		ws = WidgetScaler.getInstance(this);
		
		wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		 
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
		  	  		}	
	  	    		
	  	    		if(txAufloesung != null){
	  	    	        txAufloesung.setLayoutParams(ws.get_camera_aufloesung_layout());
	  	    		}
	  	    		
	  	    		if(btFlashlight != null) {
	  	    			btFlashlight.setLayoutParams(ws.get_camera_flashlight_layout());
	  	    			
	  	    			Log.d("#", String.valueOf(angle));
	  	    			
	  	    			animationX(btFlashlight, angle);
						    
	  	    		}
	  	    		
	  	    		if(zoomIn != null){
	  	    			zoomIn.setLayoutParams(ws.get_camera_zoom_in_layout());
	  	    		}
	  	    		
	  	    		if(zoomOut != null){
	  	    			zoomOut.setLayoutParams(ws.get_camera_zoom_out_layout());
	  	    		}
	  	    	}
	  	    }
	  	};
		
	  	txAufloesung = (android.alliance.camera.TextViewRotate) findViewById(R.id.aufloesung);
	  	txAufloesung.setLayoutParams(ws.get_camera_aufloesung_layout());
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
		animationX(btFlashlight, orientation.getAngle());
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
    	
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
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
				Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
			}
		}
		
		Parameters parameters = camera.getParameters();
		
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
		
		initZoom(parameters); 
		initFlashlight();
		
		camera.setParameters(parameters);
		
		camera.startPreview();
	}

	
	private void initZoom(Parameters params){
		if(params.isZoomSupported()){    
    	    maxZoomLevel = params.getMaxZoom();
    	    currentZoomLevel = params.getZoom();
    	    
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
	    Camera.Parameters localParameters = this.camera.getParameters();
	    localParameters.setRotation(orientation.getAngle());
	    this.camera.setParameters(localParameters);
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
				    	Log.d("#", "PhotoCallback");
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.orientationListener.enable();
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
	
	private void animationX(View target, int angle){

			 Animation an = null;
 	      
 	      if(angle == 270){
//	        	 canvas.rotate(90);
//	        	 canvas.translate(0, -getWidth());
 	    	an = new RotateAnimation(0.0f, 90.0f, target.getWidth()/2, target.getHeight()/2);
	         } else if(angle == 180){
//	        	 canvas.translate(0,  getHeight());
	        	 an = new RotateAnimation(0.0f, 180.0f, target.getWidth()/2, target.getHeight()/2);
//	        	 canvas.rotate(180);
//	        	 canvas.translate(-getWidth(), -getHeight());
	        	 
	         } else if(angle == 90){
	        	an = new RotateAnimation(lastRotation, 0.0f, target.getWidth()/2, target.getHeight()/2);
	        	lastRotation = 0.0f;
//	        	 canvas.rotate(-90);
//	        	 canvas.translate(-getHeight(), 0);
	        	 
	         } else if(angle == 0){
	        	
	        	an = new RotateAnimation(lastRotation, -90.0f, target.getWidth()/2, target.getHeight()/2);
	        	lastRotation = -0.90f;
	        	
	         }
 	      


 	      if(an != null){
 	    	 // Set the animation's parameters
			    an.setDuration(1000);               // duration in ms
			    an.setRepeatCount(0);                // -1 = infinite repeated
			    an.setRepeatMode(Animation.REVERSE); // reverses each repeat
			    an.setFillAfter(true);               // keep rotation after animation

			    // Aply animation to image view
			    target.setAnimation(an);
 	      }
	}
}
