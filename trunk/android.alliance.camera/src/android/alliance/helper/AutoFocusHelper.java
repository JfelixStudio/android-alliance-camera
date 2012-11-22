package android.alliance.helper;

import android.alliance.camera.AllianceCamera;
import android.alliance.camera.R;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;

public class AutoFocusHelper {

	public boolean available = false;
	public AutoFocusMode autoFocusMode = AutoFocusMode.AUTOFOCUS_ON;
	
	public AutoFocusHelper(Context ctx){
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		
		if(available){
			autoFocusMode = AutoFocusMode.AUTOFOCUS_ON;
		} else {
			autoFocusMode = AutoFocusMode.AUTOFOCUS_OFF;
		}
	}
	
	public void changeAutoFocusMode(AllianceCamera allianceCamera, ImageView ivAutofocus){
		if(autoFocusMode == AutoFocusMode.AUTOFOCUS_ON){
			autoFocusMode = AutoFocusMode.AUTOFOCUS_OFF;
			allianceCamera.stopAutoFocus();
			
		} else if (autoFocusMode == AutoFocusMode.AUTOFOCUS_OFF){
			autoFocusMode = AutoFocusMode.AUTOFOCUS_ON;
			allianceCamera.initAutoFokus();
		}
		
		ivAutofocus.setImageResource(autoFocusMode.drawable);
	}
	
	public enum AutoFocusMode {
		
		AUTOFOCUS_ON(R.drawable.bt_autofocus_on_selector),
		AUTOFOCUS_OFF(R.drawable.bt_autofocus_off_selector);
		
		public int drawable;
		
		private AutoFocusMode(int resId) {
			this.drawable = resId;
		}
		
	}
}
