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
		
		FrameLayout frameLayout = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		frameLayout.setBackgroundColor(Color.RED);
		addContentView(frameLayout, params);
		
		android.widget.RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		cameraWrapper = new CameraWrapper(this, frameLayout, layoutParams);
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
}