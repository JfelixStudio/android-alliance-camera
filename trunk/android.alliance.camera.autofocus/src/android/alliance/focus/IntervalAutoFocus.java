package android.alliance.focus;

import android.hardware.Camera;

public class IntervalAutoFocus extends AutoFocus {

	public IntervalAutoFocus(Camera camera, FocusView focusView) {
		super(camera, focusView);
	}

	@Override
	public void startTask() {
		task = new IntervalAutoFocusAsyncTask();
		task.execute(this, this, this);
	}
	
}
