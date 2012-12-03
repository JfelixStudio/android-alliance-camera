package android.alliance.data;

import alliance.camera.R;

public enum WhiteBalance {

	AUTO("auto", R.drawable.alliance_bt_daylight),
	INCANDESCENT("incandescent", R.drawable.alliance_bt_incandescent),
	FLUORESCENT("fluorescent", R.drawable.alliance_bt_fluorescent),
	WARM_FLUORESCENT("warm-fluorescent", R.drawable.alliance_bt_fluorescent_warm),
	DAYLIGHT("daylight", R.drawable.alliance_bt_daylight),
	CLOUDY_DAYLIGHT("cloudy-daylight", R.drawable.alliance_bt_cloudy_daylight),
	TWILIGHT("twilight", R.drawable.alliance_bt_twilight),
	SHADE("shade", R.drawable.alliance_bt_shade);
	
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
