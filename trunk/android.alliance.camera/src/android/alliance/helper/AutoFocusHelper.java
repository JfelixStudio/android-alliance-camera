package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import alliance.camera.R;
import android.alliance.focus.AutoFocus;
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

	public Float THRESHOLD = null;;
	/** is autofocus from this device supported */
	public boolean available = false;
	public AutoFocusMode autoFocusMode = AutoFocusMode.OFF;
	public AutoFocus autoFocus;
	private Context ctx;
	public List<AutoFocusMode> sequence = new ArrayList<AutoFocusMode>();
	
	public AutoFocusHelper(Context ctx) {
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
	
	public void setStartingMode(AutoFocusMode mode) {
		autoFocusMode = mode;
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
		initAutoFocus(camera);
	}
	
	/**
	 *	called from surfaceCreated() 
	 */
	public void initAutoFocus(Camera camera) {
		
		if(camera != null && available) {
			if(autoFocus == null) {
				MyFocusRectangle mFocusRectangle = (MyFocusRectangle) ((Activity)ctx).findViewById(R.id.focus_rectangle);
				autoFocus = new SensorAutoFocus(ctx, camera, mFocusRectangle, THRESHOLD);
			}
			
			if(autoFocusMode == AutoFocusMode.AUTO) {
				autoFocus.startAutoFocus();
			} else {
				autoFocus.stopAutoFocus();
			}
		}
	}
	
	public void startAutoFocus() {
		autoFocus.startAutoFocus();
	}
	
	public void stopAutoFocus() {
		if(autoFocus != null){
			if(autoFocusMode != AutoFocusMode.OFF) {
				autoFocus.stopAutoFocus();
				autoFocusMode = AutoFocusMode.OFF;
			}	
		}
	}
	
	public boolean isFocusing() {
		return autoFocus.isFocusing();
	}
	
	public void doAutoFocus() {
		autoFocus.doAutoFocus();
	}
	
	public void setAutofocusSensibility(Float value){
		this.THRESHOLD = value;
	}
}
