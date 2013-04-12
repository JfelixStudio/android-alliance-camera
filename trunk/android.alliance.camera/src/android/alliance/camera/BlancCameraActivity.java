package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import alliance.camera.R;
import android.alliance.exceptions.AllianceExceptionType;
import android.alliance.exceptions.OnException;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
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
public class BlancCameraActivity extends Activity implements OnException { //implements Callback {

	private Context ctx;
	private SurfaceView surfaceView;

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
		
		setContentView(R.layout.cameranew);

		surfaceView = (SurfaceView) findViewById(R.id.sv_camera);

		String folderPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamTest/";
		String fileName = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";

		File filePath = new File(folderPath, fileName);
		
		allianceCamera = new AllianceCamera(this, surfaceView, cameraFacing, useAlternativeFacing, filePath, this);
		allianceCamera.setPictureSizeMegapixel(3000000);
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
		allianceCamera.releaseCamera();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onException(Exception exception, String message, AllianceExceptionType type) {
		// TODO Auto-generated method stub
		
	}
	
}
