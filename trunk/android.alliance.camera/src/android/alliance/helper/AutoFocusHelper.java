package android.alliance.helper;

import alliance.camera.R;
import android.alliance.camera.AllianceCamera;
import android.alliance.focus.AutoFocus;
import android.alliance.focus.ManualAutoFocus;
import android.alliance.focus.MyFocusRectangle;
import android.alliance.focus.SensorAutoFocus;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.ImageView;

/**
 * 
 */
public class AutoFocusHelper {

	/** is autofocus from this device supported */
	public boolean available = false;
	
	public AutoFocusMode autoFocusMode = AutoFocusMode.OFF;
	
	public AutoFocus autoFocus;
	
	private Context ctx;
	private Camera camera;
	
	public AutoFocusHelper(Context ctx, AutoFocusMode mode){
		this.ctx = ctx;
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		
		if(available) {
			autoFocusMode = mode;
		} else {
			autoFocusMode = AutoFocusMode.OFF;
		}
	}
	
	public void changeAutoFocusMode(AllianceCamera allianceCamera, ImageView ivAutofocus) {
		
		switch(autoFocusMode) {
		case AUTO:
			autoFocusMode = AutoFocusMode.MANUAL;
			stopAutoFocus();
			break;
		case OFF:
			autoFocusMode = AutoFocusMode.AUTO;
			initAutoFokus(camera);
			break;
		case MANUAL:
			autoFocusMode = AutoFocusMode.OFF;
			initAutoFokus(camera);
			break;
		}
		
		ivAutofocus.setImageResource(autoFocusMode.drawable);
	}
	
	public void initAutoFokus(Camera camera) {
		this.camera = camera;
		
		if(camera != null){
			if(available && autoFocusMode != AutoFocusMode.OFF) {
				if(autoFocus == null) {
					MyFocusRectangle mFocusRectangle = (MyFocusRectangle) ((Activity)ctx).findViewById(R.id.focus_rectangle);
					
					switch(autoFocusMode) {
					case AUTO:
						autoFocus = new SensorAutoFocus(ctx, camera, mFocusRectangle);
						break;
					case MANUAL:
						autoFocus = new ManualAutoFocus(camera, mFocusRectangle);
						break;
					}
				}
				
				autoFocus.startAutoFocus();
			}	
		}
	}
	
	public void startAutoFocus() {
		autoFocus.startAutoFocus();
	}
	
	public void stopAutoFocus() {
		if(autoFocusMode != AutoFocusMode.OFF) {
			autoFocus.stopAutoFocus();
		}
	}
	
	public boolean isFocusing() {
		return autoFocus.isFocusing();
	}
	
}
