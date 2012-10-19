package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera.Size;

public class CameraPreviewSizeHelper {
	
	/**
	 * The tolerance to find sizes that belong to the same aspect ratio group (16:9, 5:3, 3:2, 4:3). <br>
	 * For example the HTC Sensation has for 16:9 three supported sizes  1.78(1280:720), 1.76(960:544), 1.67(800:480) which can get grouped with a tolerance of  0.11 
	 */
	public static double ASPECT_TOLERANCE = 0.12;

	/**
	 * good source: http://www.java2s.com/Code/Android/Hardware/
	 * Gettheoptimalpreviewsizeforthegivenscreensize.htm
	 * 
	 * @param width	of the SurfaceView
	 * @param height of the SurfaceView
	 * @param supportedPreviewSizes	the sizes reflect the orientation of the camera width > height
	 * @param aspectTolerance use CameraPreviewSizeHelper.ASPECT_TOLERANCE
	 * @return Size  
	 */
	public static Size getBestPreviewSize(int width, int height, List<Size> supportedPreviewSizes, double aspectTolerance) {

		/*
		 * if necessary, changes the values to the orientation of the camera
		 * sensor that is always in landscape what means width > height.
		 */
		if (width < height) {
			int tmpHeight = height;
			height = width;
			width = tmpHeight;
		}

		double sourceRatio = (double) width / height;
		List<Size> bestSizesOnRatioAndTolerance = new ArrayList<Size>();
		Size bestPreviewSize = null;

		sourceRatio = Math.abs(sourceRatio);

		bestSizesOnRatioAndTolerance = getBestSizesOnRatioAndTolerance(sourceRatio, supportedPreviewSizes, aspectTolerance);

		// Is there no ratios within aspectTolerance
		// get best ratio without taking into account check screen width and
		// height
		if (bestSizesOnRatioAndTolerance.isEmpty()) {
			bestPreviewSize = getBestSizeOnRatio(sourceRatio, supportedPreviewSizes);

		// Get tolerance-ratios and check with value nearest display width
		} else {
			bestPreviewSize = getBestSizeOnWith(width, bestSizesOnRatioAndTolerance);
		}

		return bestPreviewSize;
	}

	private static Size getBestSizeOnRatio(double sourceRatio, List<Size> supportedSizes) {
		Size bestSize = null;
		double lastDiff = Double.MAX_VALUE;
		
		for (Size size : supportedSizes) {
			double supportedRatio = size.width / size.height;
			
			double diff = Math.abs(sourceRatio - supportedRatio);
			if(diff < lastDiff) {
				bestSize = size;
				lastDiff = diff;
			}
		}
		
		return bestSize;
	}
	
	private static List<Size> getBestSizesOnRatioAndTolerance(double sourceRatio, List<Size> supportedSizes, double tolerance) {
		List<Size> bestSizes = new ArrayList<Size>();
		
		for (Size size : supportedSizes) {

			double supportedRatio = (double) size.width / size.height;

			if (Math.abs(sourceRatio - supportedRatio) < tolerance) {
				bestSizes.add(size);
			}
		}
		
		return bestSizes;
	}
	
	private static Size getBestSizeOnWith(int width, List<Size> supportedSizes) {
		double lastDiff = Double.MAX_VALUE;
		Size bestSize = null;

		for (Size size : supportedSizes) {

			double diff = Math.abs(width - size.width);
			if (diff < lastDiff) {
				lastDiff = diff;
				bestSize = size;
			}
		}
		
		return bestSize;
	}

}
