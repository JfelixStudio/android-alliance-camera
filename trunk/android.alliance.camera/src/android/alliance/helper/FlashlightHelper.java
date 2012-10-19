package android.alliance.helper;

import android.hardware.Camera.Parameters;

public class FlashlightHelper {

	public static FlashLightStatus flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
	
	public static Parameters setFlashlightOn(Parameters params){
		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		return params;
	}
	
	public static Parameters setFlashlightAuto(Parameters params){
		params.setFlashMode(Parameters.FLASH_MODE_ON);
		return params;
	}
	
	public static Parameters setFlashlightOff(Parameters params){
		params.setFlashMode(Parameters.FLASH_MODE_OFF);
		return params;
	}
	
	public enum FlashLightStatus {
		FLASHLIGHT_AUTO,
		FLASHLIGHT_ON,
		FLASHLIGHT_OFF;
	}
}
