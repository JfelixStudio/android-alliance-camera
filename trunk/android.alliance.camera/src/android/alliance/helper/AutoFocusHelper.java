package android.alliance.helper;

import android.alliance.camera.R;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;

public class AutoFocusHelper {

	public boolean available = false;
	public AutoFocusMode autoFocusMode = AutoFocusMode.AUTOFOCUS_ON;
	
	public AutoFocusHelper(Context ctx){
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
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
