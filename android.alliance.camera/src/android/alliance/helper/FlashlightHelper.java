package android.alliance.helper;

import android.alliance.camera.R;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;

public class FlashlightHelper {

	public static FlashLightStatus flashlightStatus = FlashLightStatus.FLASHLIGHT_AUTO;
	
	public static Parameters setFlashMode(Parameters params, ImageView imageView) {
		flashlightStatus.setFlashMode(params);
		imageView.setImageResource(flashlightStatus.drawable);
		return params;
	}
	
	public static Parameters nextFlashMode(Parameters params) {
		
		// TODO die logik hier rein und nicht in die UICamera 
		
		return params;
	}
	
	public enum FlashLightStatus {
		
		FLASHLIGHT_AUTO(Parameters.FLASH_MODE_ON, R.drawable.bt_flashlight_auto),
		FLASHLIGHT_ON(Parameters.FLASH_MODE_TORCH, R.drawable.bt_flashlight_on),
		FLASHLIGHT_OFF(Parameters.FLASH_MODE_OFF, R.drawable.bt_flashlight_off);
		
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
