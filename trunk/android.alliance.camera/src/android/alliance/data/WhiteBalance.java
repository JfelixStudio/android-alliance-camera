package android.alliance.data;

import android.alliance.camera.R;

public enum WhiteBalance {

	AUTO("auto", R.drawable.bt_daylight),
	INCANDESCENT("incandescent", R.drawable.bt_incandescent),
	FLUORESCENT("fluorescent", R.drawable.bt_fluorescent),
	WARM_FLUORESCENT("warm-fluorescent", R.drawable.bt_fluorescent),
	DAYLIGHT("daylight", R.drawable.bt_daylight),
	CLOUDY_DAYLIGHT("cloudy-daylight", R.drawable.bt_cloudy_daylight),
	TWILIGHT("twilight", R.drawable.bt_platzhalter),
	SHADE("shade", R.drawable.bt_platzhalter);
	
	public String parameter;
	public int icon;
	
	private WhiteBalance(String parameter, int icon) {
		this.parameter = parameter;
		this.icon = icon;
	}

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static WhiteBalance getWhiteBalance(String parameter) {
		
		for(WhiteBalance wb : values()) {
			if(wb.parameter.equalsIgnoreCase(parameter)) {
				return wb;
			}
		}
		
		return AUTO;
	}
	
}
