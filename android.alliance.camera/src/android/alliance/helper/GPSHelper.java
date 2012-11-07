package android.alliance.helper;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;

public class GPSHelper {

	private static GPSHelper instance;
	
	public static GPSHelper getInstance(){
		if(instance == null){
			instance = new GPSHelper();
		}
		
		return instance;
	}
	
	public void enableGps(Context ctx){
		Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		ctx.sendBroadcast(intent);
	}
	
	public void disableGps(Context ctx){
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", false);
		ctx.sendBroadcast(intent);
	}
	
	public Criteria getBestGpsProvider(){
		 Criteria c = new Criteria();
	     c.setAccuracy(Criteria.ACCURACY_FINE);
	     c.setAltitudeRequired(false);
	     c.setBearingRequired(false);
	     c.setSpeedRequired(false);
	     c.setCostAllowed(true);
	     c.setPowerRequirement(Criteria.POWER_HIGH);
	        
	     return c;
	 }
	
}
