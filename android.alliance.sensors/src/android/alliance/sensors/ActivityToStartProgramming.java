package android.alliance.sensors;

import alliance.sensors.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ActivityToStartProgramming extends Activity {

	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		// setContentView(R.layout.x);

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
	}

	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}

}
