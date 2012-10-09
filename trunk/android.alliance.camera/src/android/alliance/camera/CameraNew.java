package android.alliance.camera;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CameraNew extends Activity implements Callback {

	public Camera camera;
	private SurfaceView surfaceView;
	private Parameters parameters;
	private Display display = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Muß aufgerufen werden, bevor Inhalte der Kamera zugewiesen werden
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		setContentView(R.layout.cameranew);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);
		surfaceView.getHolder().addCallback(this);

		/**
		 * Diese Zeile ist echt interessant! Denn wenn man sie auskommentiert, gibt es kein Preview! 
		 * Hab eben bestimmt ne viertel Stunde gesucht, wieso ich kein Preview hatte.
		 * 
		 * Allerdings ist sie deprecated und in der Methode steht:
		 * @deprecated this is ignored, this value is set automatically when needed.
		 * 
		 * Da sag ich nur ROFL
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}

	private void initCamera(SurfaceHolder holder){
		try{
			
			boolean backCamAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
			
			if(backCamAvailable){
				
				if(camera == null){
					camera = Camera.open();	
				}
				
				camera.setPreviewDisplay(holder);					
			}			

		} catch (Exception e){
			camRelease();

			/** TODO
			 * Hier Kamera nochmal rekursiv neu initialisieren, oder
			 * aber einen Fehlermeldung zurückgeben, dass sie nicht
			 * richtig initialisiert wurde
			 */
		}
	}
	
	private void initCameraPreferences(){
		if(camera != null){
			
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
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	protected void onStop() {
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
		initCamera(holder);
		initCameraPreferences();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camRelease();
	}
}
