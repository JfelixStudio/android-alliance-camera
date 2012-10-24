package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.alliance.data.VOResolution;
import android.hardware.Camera.Size;

public class ResolutionHelper {

	public Size selectedResolution = null;
	public List<VOResolution> lSupportedPictureSizes = new ArrayList<VOResolution>();
	private static ResolutionHelper instance;
	
	public static ResolutionHelper getInstance(){
		if(instance == null){
			instance = new ResolutionHelper();
		}
		
		return instance;
	}
	
	public void initSupportedScreenSizes(List<Size> supportedSizes){
		
		lSupportedPictureSizes = new ArrayList<VOResolution>();
		
		int i = 0;
		for(Size s : supportedSizes){
			int mp = s.width * s.height;
			VOResolution cm = new VOResolution(i, s, mp);
			lSupportedPictureSizes.add(cm);
			i++;
		}
	}
	
	public void setMegaPixelSizeOnDefault(int megapixel) {

		int lastDiff = Integer.MAX_VALUE;
		
		if (lSupportedPictureSizes != null) {
			for (VOResolution res : lSupportedPictureSizes) {
				int diff = Math.abs(megapixel - res.getMegapixel());
				
				if (diff < lastDiff) {
					lastDiff = diff;
					selectedResolution = res.getSize();
				}
			}
		}
	}
}
