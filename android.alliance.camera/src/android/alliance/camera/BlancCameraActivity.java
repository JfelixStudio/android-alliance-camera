package android.alliance.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author alliance
 *
 */
public class BlancCameraActivity extends Activity { //implements Callback {

	private Context ctx;
	private SurfaceView surfaceView;
//	private Display display = null;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1 */
	private Integer cameraFacing = null;

	private boolean useAlternativeFacing = false;
	
	private AllianceCamera allianceCamera;
	
	// Activity livecycle ///////////////////////////////
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			cameraFacing = extras.getInt(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
			useAlternativeFacing = extras.getBoolean(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
		}
		
//		display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		setContentView(R.layout.cameranew);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);

		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing);
	}

	@Override
	protected void onStart() {
		Log.d("#", "onStart()");
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		Log.d("#", "onRestart()");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("#", "onStop()");
		super.onStop();
		
		// TODO: sollte das nicht in onPause()? Ist aber schon in onSurfaceDestroyed
		allianceCamera.camRelease();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}
	
}