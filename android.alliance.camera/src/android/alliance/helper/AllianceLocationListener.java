package android.alliance.helper;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class AllianceLocationListener implements LocationListener{

	private Camera camera = null;
	public AllianceLocationListener(Camera camera){
		this.camera = camera;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		try{
			if(camera != null){
				Parameters param = camera.getParameters();
				
				param.removeGpsData();
				
				param.setGpsLatitude(location.getLatitude());
				param.setGpsLongitude(location.getLongitude());
				param.setGpsAltitude(location.getAltitude());
				param.setGpsTimestamp(location.getTime());
				
				camera.setParameters(param);
			}	
		} catch (Exception e){
			// do nothing
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}
	

}
