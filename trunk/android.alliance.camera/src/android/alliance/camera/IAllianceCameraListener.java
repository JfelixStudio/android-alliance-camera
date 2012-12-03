package android.alliance.camera;

import alliance.camera.R;

/**
 * For additional life cycle callbacks that add to the UI-Activity and originate in the AllianceCamera.  
 * 
 * @author alliance
 *
 */
public interface IAllianceCameraListener {

	/**
	 * Called after surfaceCreated() is finished. 
	 */
	public void onCameraCreated();
	
	/**
	 * Set the activityResultCode to RESULT_OK after photo is taken. Default is RESULT_CANCELED
	 */
	public void afterPhotoTaken();
}
