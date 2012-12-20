package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import alliance.camera.R;
import android.alliance.camera.AllianceCamera;
import android.alliance.focus.AutoFocus;
import android.alliance.focus.ManualAutoFocus;
import android.alliance.focus.MyFocusRectangle;
import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.FlashlightHelper.FlashMode;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
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
	public List<AutoFocusMode> sequence = new ArrayList<AutoFocusMode>();
	
	public AutoFocusHelper(Context ctx){
		this.ctx = ctx;
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
	}
	
	public void setAutoFocusMode(Camera camera, ImageView ivAutofocus) {
		initAutoFocus(camera);
		ivAutofocus.setImageResource(autoFocusMode.drawable);
	}
	
	/**
	 * To build up the individual sequence of autofocus modes 
	 * @param stati
	 */
	public void addToSequence(AutoFocusMode stati) {
		sequence.add(stati);
	}
	
	/*
	 * Setting Autofocus on Click
	 */
	public void next(Camera camera, ImageView ivFlashlight) {
		for(int i=0; i<sequence.size(); i++) {
			AutoFocusMode statiAtI = sequence.get(i);
			if(statiAtI == autoFocusMode) {
				if(i == sequence.size()-1) {
					autoFocusMode = sequence.get(0);
					break;
				} else {
					autoFocusMode = sequence.get(i+1);
					break;
				}
			}
		}
		setAutoFocusMode(camera, ivFlashlight);
	}
	
	public void initAutoFocus(Camera camera) {
		
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
			} else {
				autoFocus.setAutoFocusOff();
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
