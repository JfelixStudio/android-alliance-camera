package android.alliance.focus;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

public abstract class AutoFocus implements Camera.AutoFocusCallback {

	private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;

	public int mFocusState = FOCUS_NOT_STARTED;
	
	public Camera camera;
	public FocusView focusView;
	protected AsyncTask<AutoFocus, Void, AutoFocus> task;
	

	public AutoFocus(Camera camera, FocusView focusView) {
		this.camera = camera;
		this.focusView = focusView;
	}
	
	/** gets called after auto focus finishes successfully */
	public void startAutoFocus() {
		updateFocusIndicator();
		startTask();
	}
	
	/** Gets called when startAutoFocus() is completed.<br> 
	 * Create a AsyncTask and execute it. */
	public abstract void startTask();
	
	public void stopAutoFocus() {
		if(task != null) {
			task.cancel(true);
		}
		
		mFocusState = FOCUS_NOT_STARTED;
		updateFocusIndicator();
	}
	
	public void doAutoFocus() {
		
		if(mFocusState != FOCUSING) {
			mFocusState = FOCUSING;
			updateFocusIndicator();
	
			if(camera != null) {
				try{
					camera.autoFocus(this);	
				} catch(RuntimeException e){
					Log.d("#", "AutoFokus: " + e.getMessage());
				}
			}
		}
	}

	public void updateFocusIndicator() {
        if (focusView == null){
        	return;
        }

        if (mFocusState == FOCUSING || mFocusState == FOCUSING_SNAP_ON_FINISH) {
        	focusView.showStart();
        } else if (mFocusState == FOCUS_SUCCESS) {
        	focusView.showSuccess();
        } else if (mFocusState == FOCUS_FAIL) {
        	focusView.showFail();
        } else {
        	focusView.clear();
        }
        
    }
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public boolean isFocusing() {
		if(mFocusState == FOCUSING) {
			return true;
		}
		return false;
	}
	
	public void clearFocusView(){
		if(focusView != null) {
			focusView.clear();
		}
	}
	
	public void onAutoFocusSuccess() {
		
	}
	
	public void onAutoFocusFailure() {
		
	}
	
	// Camera.AutoFocusCallback //////////////

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		
		if(success){
	    	mFocusState = FOCUS_SUCCESS;
	    	onAutoFocusSuccess();
	    } else {
	    	mFocusState = FOCUS_FAIL;
	    	onAutoFocusFailure();
	    }
	    
		updateFocusIndicator();
	}
	
//	public void setAutoFocusOff(){
//		stopAutoFocus();
//		mFocusState = FOCUS_NOT_STARTED;
//		updateFocusIndicator();
//	}
		
}
