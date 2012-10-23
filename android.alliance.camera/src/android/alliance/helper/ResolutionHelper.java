package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.alliance.data.VOResolution;
import android.hardware.Camera.Size;

public class ResolutionHelper {

	private static ResolutionHelper instance;
	
	public Size selectedResolution = null;
	public List<VOResolution> lSupportedPictureSizes = new ArrayList<VOResolution>();
	
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
			VOResolution cm = new VOResolution(i, s);
			lSupportedPictureSizes.add(cm);
			i++;
		}
	}
	
	public void set3MegaPixelSizeOnDefault() {

		if (lSupportedPictureSizes != null) {

			for (VOResolution res : lSupportedPictureSizes) {
				int mp = res.getSize().width * res.getSize().height;

				if (mp < 1500000) {
					selectedResolution = res.getSize();
				}
			}
		}

		if(selectedResolution == null){
			selectedResolution = lSupportedPictureSizes.get(0).getSize();
		}
	}
	
}
