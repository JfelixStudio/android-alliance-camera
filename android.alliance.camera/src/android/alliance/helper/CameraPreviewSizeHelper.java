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

		/*
		 * if necessary, changes the values to the orientation of the camera
		 * sensor that is always in landscape what means width > height.
		 */
		if (width < height) {
			int tmpHeight = height;
			height = width;
			width = tmpHeight;
		}

		double aspectTolerance = 0.12;
		double sourceRatio = (double) width / height;
		List<Size> lFilteredSizes = new ArrayList<Size>();
		Size bestPreviewSize = null;

		sourceRatio = Math.abs(sourceRatio);

		// Check best ratio with respect to aspectTolerance
		for (Size size : supportedPreviewSizes) {

			double supportedRatio = (double) size.width / size.height;

			if (Math.abs(sourceRatio - supportedRatio) < aspectTolerance) {
				lFilteredSizes.add(size);
			}
		}

		// Is there no ratios within aspectTolerance
		// get best ratio without taking into account check screen width and
		// height
		if (lFilteredSizes.size() == 0) {
			bestPreviewSize = getBestSizeOnRatio(sourceRatio, supportedPreviewSizes);

			// Get tolerance-ratios and check with value nearest display width
			// and height
		} else {

			double lastDiff = Double.MAX_VALUE;

			for (Size size : lFilteredSizes) {

				double diff = Math.abs(width - size.width);
				if (diff < lastDiff) {
					lastDiff = diff;
					bestPreviewSize = size;
				}
			}
		}

		return bestPreviewSize;
	}

	private static Size getBestSizeOnRatio(double sourceRatio, List<Size> supportedPreviewSizes) {
		Size bestSize = null;
		double lastDiff = Double.MAX_VALUE;
		
		for (Size size : supportedPreviewSizes) {
			double supportedRatio = size.width / size.height;
			
			double diff = Math.abs(sourceRatio - supportedRatio);
			if(diff < lastDiff) {
				bestSize = size;
				lastDiff = diff;
			}
		}
		
		return bestSize;
	}

}
