package android.alliance.helper;

import android.alliance.camera.AllianceCamera;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.ImageView;

/**
 * 
 */
public class AutoFocusHelper {

	/** is autofocus from this device supported */
	public boolean available = false;
	
	public AutoFocusMode autoFocusMode = AutoFocusMode.OFF;
	
	public AutoFocusHelper(Context ctx){
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		
		if(available) {
			autoFocusMode = AutoFocusMode.ON;
		} else {
			autoFocusMode = AutoFocusMode.OFF;
		}
	}
	
	public void changeAutoFocusMode(AllianceCamera allianceCamera, ImageView ivAutofocus) {
		
		switch(autoFocusMode) {
		case ON:
			autoFocusMode = AutoFocusMode.OFF;
			allianceCamera.stopAutoFocus();
			break;
		case OFF:
			autoFocusMode = AutoFocusMode.ON;
			allianceCamera.initAutoFokus();
			break;
		case MANUAL:
			autoFocusMode = AutoFocusMode.ON;
			allianceCamera.initAutoFokus();
			break;
		}
		
		ivAutofocus.setImageResource(autoFocusMode.drawable);
	}
	
}
