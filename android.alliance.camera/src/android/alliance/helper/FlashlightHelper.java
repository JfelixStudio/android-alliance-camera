package android.alliance.helper;

import android.alliance.camera.R;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;

public class FlashlightHelper {
	
	public FlashLightStatus flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
	public boolean available = false; 
			
	public void init(Activity activity){
		available = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	public Parameters setFlashMode(Parameters params, ImageView imageView) {
		flashlightStatus.setFlashMode(params);
		imageView.setImageResource(flashlightStatus.drawable);
		return params;
	}
	
	public String getFlashlightMode(){
		
		String flashMode = null;
		
		for(FlashLightStatus fls : FlashLightStatus.values()){
			if(fls.equals(flashlightStatus)){
				flashMode = fls.flashMode;
				break;
			}
		}
		
		return flashMode;
	}
	
	/*
	 * Setting Flashlight on Click. If Flashlight: auto => set
	 * status. Check the mode in AllianceCamera on photo capture()
	 * otherwise => set status and set FlashLightType-Mode to
	 * Camera-Parameters
	 */
	public Parameters nextFlashMode(Parameters param, ImageView ivFlashlight) {
		
		if (flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_AUTO)) {
			flashlightStatus = FlashLightStatus.FLASHLIGHT_ON;
			setFlashMode(param, ivFlashlight);
		} else if (flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_ON)) {
			flashlightStatus = FlashLightStatus.FLASHLIGHT_OFF;
			setFlashMode(param, ivFlashlight);
		} else if (flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_OFF)) {
			flashlightStatus = FlashLightStatus.FLASHLIGHT_TORCH;
			setFlashMode(param, ivFlashlight);
		} else if (flashlightStatus.equals(FlashLightStatus.FLASHLIGHT_TORCH)) {
			flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
			setFlashMode(param, ivFlashlight);
		}
		
		return param;
	}
	
	public enum FlashLightStatus {
		
		FLASHLIGHT_AUTO(Parameters.FLASH_MODE_AUTO, R.drawable.bt_flashlight_auto_selector),
		FLASHLIGHT_ON(Parameters.FLASH_MODE_ON, R.drawable.bt_flashlight_on_selector),
		FLASHLIGHT_OFF(Parameters.FLASH_MODE_OFF, R.drawable.bt_flashlight_off_selector),
		FLASHLIGHT_TORCH(Parameters.FLASH_MODE_TORCH, R.drawable.bt_flashlight_torch_selector);
		
		public String flashMode;
		public int drawable;
		
		private FlashLightStatus(String flashMode, int drawable) {
			this.flashMode = flashMode;
			this.drawable = drawable;
		}
		
		public Parameters setFlashMode(Parameters params) {
			params.setFlashMode(flashMode);
			return params;
		}
	}
}
