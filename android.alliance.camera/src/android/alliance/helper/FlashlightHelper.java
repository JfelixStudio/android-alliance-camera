package android.alliance.helper;

import android.hardware.Camera.Parameters;

public class FlashlightHelper {

	public static FlashLightStatus flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
	
	public static Parameters setFlashMode(Parameters params) {
		flashlightStatus.setFlashMode(params);
		return params;
	}
	
	public static Parameters nextFlashMode(Parameters params) {
		
		// TODO die logik hier rein und nicht in die UICamera 
		
		return params;
	}
	
	public enum FlashLightStatus {
		
		FLASHLIGHT_AUTO(Parameters.FLASH_MODE_ON),
		FLASHLIGHT_ON(Parameters.FLASH_MODE_TORCH),
		FLASHLIGHT_OFF(Parameters.FLASH_MODE_OFF);
		
		public String flashMode;
		
		private FlashLightStatus(String flashMode) {
			this.flashMode = flashMode;
		}
		
		public Parameters setFlashMode(Parameters params) {
			params.setFlashMode(flashMode);
			return params;
		}
	}
}
