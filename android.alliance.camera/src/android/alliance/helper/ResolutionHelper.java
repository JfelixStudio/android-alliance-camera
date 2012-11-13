package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.alliance.data.Resolution;
import android.hardware.Camera.Size;

public class ResolutionHelper {

	public Size selectedResolution = null;
	public List<Resolution> supportedPictureSizes = new ArrayList<Resolution>();
	private static ResolutionHelper instance;
	
	public static ResolutionHelper getInstance(){
		if(instance == null){
			instance = new ResolutionHelper();
		}
		
		return instance;
	}
	
	public void initSupportedScreenSizes(List<Size> supportedSizes){
		
		supportedPictureSizes = new ArrayList<Resolution>();
		
		int i = 0;
		for(Size size : supportedSizes){
			int megapixel = size.width * size.height;
			Resolution resolution = new Resolution(i, size, megapixel);
			supportedPictureSizes.add(resolution);
			i++;
		}
	}
	
	public void setMegaPixelSize(int megapixel) {

		int lastDiff = Integer.MAX_VALUE;
		
		if (supportedPictureSizes != null) {
			for (Resolution res : supportedPictureSizes) {
				int diff = Math.abs(megapixel - res.getMegapixel());
				
				if (diff < lastDiff) {
					lastDiff = diff;
					selectedResolution = res.getSize();
				}
			}
		}
	}
	
//	public void setSize(int width, int height) {
//		selectedResolution = CameraPreviewSizeHelper.getBestPreviewSize(width, height, supportedPictureSizes, CameraPreviewSizeHelper.ASPECT_TOLERANCE);
//	}
}
