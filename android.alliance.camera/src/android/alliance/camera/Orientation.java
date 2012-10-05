package android.alliance.camera;

import android.alliance.camera.R;
import android.util.Log;

public class Orientation {
	
	private static Orientation instance;
	private int angle = 0;
	
	public static Orientation getInstance(){
		if(instance == null){
			instance = new Orientation();
		}
		
		return instance;
	}

	public int getAngle() {
	    return this.angle;
	}

	public void setAngle(int paramInt) {
		if (paramInt != this.angle){
			this.angle = paramInt;	
		}
	}

	public void update(float paramFloat) {
    
//		Log.d("#", String.valueOf(paramFloat));
		
		if ((paramFloat > 0.0F) && (paramFloat <= 45.0F)){
				setAngle(90);	

		} else if ((paramFloat > 45.0F) && (paramFloat <= 135.0F)) {
			setAngle(180);
	  
		} else if ((paramFloat > 135.0F) && (paramFloat <= 225.0F)) {
			setAngle(270);
	  
		} else if ((paramFloat > 225.0F) && (paramFloat <= 315.0F)) {
			setAngle(0);
	  
		} else {
			setAngle(90);
		}
	}
}