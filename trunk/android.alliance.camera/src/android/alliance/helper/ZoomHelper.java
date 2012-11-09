package android.alliance.helper;

import android.hardware.Camera.Parameters;

public class ZoomHelper {

	public int currentZoomLevel = 0;
	public int maxZoomLevel = 0;
	public boolean mZoomSupported;
	
	public Parameters zoomIn(Parameters param){
		
		currentZoomLevel++;
		
		if (currentZoomLevel > maxZoomLevel) {
			currentZoomLevel = maxZoomLevel;
		}
		param.setZoom(currentZoomLevel);
		
		return param;
	}
	
	public Parameters zoomOut(Parameters param){
		
		currentZoomLevel--;
		
		if (currentZoomLevel < 0) {
			currentZoomLevel = 0;
		}

		param.setZoom(currentZoomLevel);
		
		return param;
	}

	// called from surfaceCreated()
	public void initZoom(Parameters param){
		mZoomSupported = param.isZoomSupported();
		
		if(mZoomSupported){
			maxZoomLevel = param.getMaxZoom();
			currentZoomLevel = param.getZoom();
		}
	}
}
