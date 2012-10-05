package android.alliance.focus;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

public class IntervalAutoFocus implements Camera.AutoFocusCallback {
	
	private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;

	public int mFocusState = FOCUS_NOT_STARTED;
	
	public Camera camera;
	public MyFocusRectangle mFocusRectangle;
	public FocusView focusView;
	private IntervalTask task;
	
	public IntervalAutoFocus(Camera camera, FocusView focusView) {
		this.camera = camera;
		this.focusView = focusView;
	}
	
	public void startAutoFocus() {
		updateFocusIndicator();
		
		task = new IntervalTask();
		task.execute(camera);
	}
	
	public void stopAutoFocus() {
		task.cancel(true);
	}
	
	private void autoFocus() {
		mFocusState = FOCUSING;
		updateFocusIndicator();
		
		camera.autoFocus(this);
	}

	public void updateFocusIndicator() {
        if (mFocusRectangle == null){
        	return;
        }

        if (mFocusState == FOCUSING || mFocusState == FOCUSING_SNAP_ON_FINISH) {
            mFocusRectangle.showStart();
        } else if (mFocusState == FOCUS_SUCCESS) {
            mFocusRectangle.showSuccess();
        } else if (mFocusState == FOCUS_FAIL) {
            mFocusRectangle.showFail();
        } else {
            mFocusRectangle.clear();
        }
    }
	
	// Camera.AutoFocusCallback //////////////

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		Log.d("#", Boolean.toString(success));
		
		if(success){
        	mFocusState = FOCUS_SUCCESS;
        	
        	startAutoFocus();
        } else {
        	mFocusState = FOCUS_FAIL;
        }
        
		updateFocusIndicator();
	}
	
	private class IntervalTask extends AsyncTask<Camera, Void, Camera> {

		@Override
		protected Camera doInBackground(Camera... params) {
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Camera camera) {
			autoFocus();
        }
		
	}
	
}
