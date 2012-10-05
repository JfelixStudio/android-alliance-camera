package android.alliance.helper;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSizeHelper {

	private static ScreenSizeHelper instance;
	private static Integer height = null;
	private static Integer width = null;
	private static Integer density = null;
	
	public static ScreenSizeHelper getInstance(){
		if(instance == null){
			instance = new ScreenSizeHelper();
		}
		
		return instance;
	}
	
	public int getDisplayHeight(Activity activity){
		
		if(height == null || width == null || density == null){
			WindowManager wm = activity.getWindowManager();
			
			Display display = wm.getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();

			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			density = outMetrics.densityDpi;
		}

		if(height > width){
			return height;
		} else {
			return width;
		}
	}
	
	public int getDensity(){
		return density;
	}
}
