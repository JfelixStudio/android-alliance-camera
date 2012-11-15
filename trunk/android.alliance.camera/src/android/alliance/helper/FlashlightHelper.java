package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.alliance.camera.R;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;

public class FlashlightHelper {
	
	public FlashMode flashStatus = FlashMode.FLASH_AUTO;
	public boolean available = false; 
	public List<FlashMode> sequence = new ArrayList<FlashMode>();

	
	public FlashlightHelper(Context ctx) {
		available = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	public Parameters setFlashMode(Parameters params, ImageView imageView) {
		flashStatus.setFlashMode(params);
		imageView.setImageResource(flashStatus.drawable);
		return params;
	}
	
	/*
	 * Setting Flashlight on Click. If Flashlight: auto => set
	 * status. Check the mode in AllianceCamera on photo capture()
	 * otherwise => set status and set FlashLightType-Mode to
	 * Camera-Parameters
	 */
	public Parameters next(Parameters param, ImageView ivFlashlight) {
		for(int i=0; i<sequence.size(); i++) {
			FlashMode statiAtI = sequence.get(i);
			if(statiAtI == flashStatus) {
				if(i == sequence.size()-1) {
					flashStatus = sequence.get(0);
					break;
				} else {
					flashStatus = sequence.get(i+1);
					break;
				}
			}
		}
		setFlashMode(param, ivFlashlight);
		return param;
	}
	
	/**
	 * To build up the individual sequence of flashlight modes 
	 * @param stati
	 */
	public void addToSequence(FlashMode stati) {
		sequence.add(stati);
	}
	
	public enum FlashMode {
		
		FLASH_AUTO(Parameters.FLASH_MODE_AUTO, R.drawable.bt_flashlight_auto_selector),
		FLASH_ON(Parameters.FLASH_MODE_ON, R.drawable.bt_flashlight_on_selector),
		FLASH_OFF(Parameters.FLASH_MODE_OFF, R.drawable.bt_flashlight_off_selector),
		FLASH_TORCH(Parameters.FLASH_MODE_TORCH, R.drawable.bt_flashlight_torch_selector);
		
		public String flashMode;
		public int drawable;
		
		private FlashMode(String flashMode, int resId) {
			this.flashMode = flashMode;
			this.drawable = resId;
		}
		
		public Parameters setFlashMode(Parameters params) {
			params.setFlashMode(flashMode);
			return params;
		}
	}
}
