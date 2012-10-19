package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera.Size;

public class CameraPreviewSizeHelper {

	/**
	 * good source: http://www.java2s.com/Code/Android/Hardware/
	 * Gettheoptimalpreviewsizeforthegivenscreensize.htm
	 * 
	 * @param width
	 * @param height
	 * @param supportedPreviewSizes
	 * @return
	 */
	public static Size getBestPreviewSize(int width, int height, List<Size> supportedPreviewSizes) {
		
		Size bestPreviewSize = null;
		
		List<Size> lFilteredSizes = new ArrayList<Size>();
		
		double sourceRatio;
		double aspectTolerance = 1.1d;
		
		if (width < height) {
			sourceRatio = (double) width / height;
		} else {
			sourceRatio = (double) height / width;
		}

		sourceRatio = Math.abs(sourceRatio);
		
		// Check best ratio with respect to aspectTolerance
		for (Size size : supportedPreviewSizes) {

			double targetRatio = getTargetRatio(size);

			if(Math.abs(sourceRatio - targetRatio) < aspectTolerance){
				lFilteredSizes.add(size);
			}
		}
		
		// Is there no ratios within aspectTolerance
		// get best ratio without taking into account check screen width and height
		if(lFilteredSizes.size() == 0){
			bestPreviewSize = getBestSizeOnRatio(sourceRatio, supportedPreviewSizes);
		
		// Get tolerance-ratios and check with value nearest display width and height
		} else {
			bestPreviewSize = getBestSizeOnSourceWithAndHeight(width, height, lFilteredSizes);
		}

		return bestPreviewSize;
	} 
	
	

	private static double getTargetRatio(Size size){
		double targetRatio = 0.0d;

		if (size.width > size.height) {
			targetRatio = (double) size.width / size.height;
		} else {
			targetRatio = (double) size.height / size.width;
		}

		return Math.abs(targetRatio);
	}
	
	private static Size getBestSizeOnRatio(double sourceRatio, List<Size> supportedPreviewSizes){

		int index = 0;
		double lastRatioToCheck = 0.0d;
		
		for(Size size : supportedPreviewSizes){
			
			double targetRatio = getTargetRatio(size);
			
			if (lastRatioToCheck == 0.0d) {
				lastRatioToCheck = targetRatio;
				index = 0;

			} else {
				double chkLastTarget = Math.abs(lastRatioToCheck);

				if (Math.abs(sourceRatio - targetRatio) < Math.abs(sourceRatio - chkLastTarget)) {
					lastRatioToCheck = targetRatio;
					index = supportedPreviewSizes.indexOf(targetRatio);
				}
			}
		}
		
		return supportedPreviewSizes.get(index);
	}
	
	private static Size getBestSizeOnSourceWithAndHeight(int sourceWidth, int sourceHeight, List<Size> lTargetSizes){
		
		int index = 0;
		Size lastSizeToCheck = null;
		
		for(Size size : lTargetSizes){
			
			if(lastSizeToCheck == null){
				lastSizeToCheck = size;
				index = 0;
			
			} else {
				
				// Re-Check sourceWidth/sourceHeight and targetWidth/targetHeight
				// minor values are with, larger values are height
				int newSourceWidth = 0;
				int newSourceHeight = 0;
				
				if(sourceWidth < sourceHeight){
					newSourceWidth = sourceWidth;
					newSourceHeight = sourceHeight;
				} else {
					newSourceWidth = sourceHeight;
					newSourceHeight = sourceWidth;
				}
				
				int newTargetWidth = 0;
				int newTargetHeight = 0;
				
				if(size.width < size.height){
					newTargetWidth = size.width;
					newTargetHeight = size.height;
				} else {
					newTargetWidth = size.height;
					newTargetHeight = size.width;
				}
				
				int newLastCheckWidth = 0;
				int newLastCheckHeight = 0;
				
				if(lastSizeToCheck.width < lastSizeToCheck.height){
					newLastCheckWidth = lastSizeToCheck.width;
					newLastCheckHeight = lastSizeToCheck.height;
				} else {
					newLastCheckWidth = lastSizeToCheck.height;
					newLastCheckHeight = lastSizeToCheck.width;
				}
				
				if(Math.abs(newSourceWidth - newTargetWidth) < Math.abs(newSourceWidth - newLastCheckWidth) &&
						Math.abs(newSourceHeight - newTargetHeight) < Math.abs(newSourceHeight - newLastCheckHeight)){
					
					lastSizeToCheck = size;
					index = lTargetSizes.indexOf(size);
				}
			}
		}
		
		return lastSizeToCheck;
	}
}
