package android.alliance.camera;

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
}
