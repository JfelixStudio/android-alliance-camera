package android.alliance.camera;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

public class CameraWrapperActivity extends Activity {

	private CameraWrapper cameraWrapper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		RelativeLayout relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		relativeLayout.setBackgroundColor(Color.RED);
		addContentView(relativeLayout, params);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 500);
		layoutParams.leftMargin = 100;
		layoutParams.topMargin = 100;
		cameraWrapper = new CameraWrapper(this, relativeLayout, layoutParams);
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
		cameraWrapper.onResume();
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
		cameraWrapper.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// check if the camera hase something to go back to
		if(cameraWrapper.onBackPressed()) {
			super.onBackPressed();
		} 
	}
}
