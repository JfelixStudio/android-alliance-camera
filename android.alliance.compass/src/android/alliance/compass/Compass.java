package android.alliance.compass;
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Compass extends Activity implements SensorEventListener {
	 
	  private Float azimut;  // View to draw a compass
	  private CustomDrawableView mCustomDrawableView;
	  private SensorManager mSensorManager;
	  private Sensor accelerometer;
	  private Sensor magnetometer;
	  
	  public class CustomDrawableView extends View {
	    Paint paint = new Paint();
	    
	    public CustomDrawableView(Context context) {
		      super(context);
		      paint.setColor(0xff00ff00);
		      paint.setStyle(Style.STROKE);
		      paint.setStrokeWidth(2);
		      paint.setAntiAlias(true);
	    };
	 
	    protected void onDraw(Canvas canvas) {
		      int width = getWidth();
		      int height = getHeight();
		      int centerx = width/2;
		      int centery = height/2;
		      
		      canvas.drawLine(centerx, 0, centerx, height, paint);
		      canvas.drawLine(0, centery, width, centery, paint);
		      
		      // Rotate the canvas with the azimut      
		      if (azimut != null){
		    	  canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
		      }
		      
		      
		      
		      paint.setColor(0xff0000ff);
		      canvas.drawLine(centerx, -1000, centerx, +1000, paint);
		      canvas.drawLine(-1000, centery, 1000, centery, paint);
		      canvas.drawText("N", centerx+5, centery-10, paint);
		      canvas.drawText("S", centerx-10, centery+15, paint);
		      
		      paint.setColor(0xff00ff00);
	    }
	  }
	 
	 
	 
	  protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	   
		  mCustomDrawableView = new CustomDrawableView(this);
		  setContentView(mCustomDrawableView);    // Register the sensor listeners
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	  }
	 
	  protected void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
	    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	  }
	 
	  protected void onPause() {
	    super.onPause();
	    mSensorManager.unregisterListener(this);
	  }
	 
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
	 
		  float[] mGravity;
		  float[] mGeomagnetic;
		  
		  public void onSensorChanged(SensorEvent event) {
		  
			  if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				  mGravity = event.values;
			  }
				  
		    
			  if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				  mGeomagnetic = event.values;  
			  }
		      
			  
			  if (mGravity != null && mGeomagnetic != null) {
				  
			      float R[] = new float[9];
			      float I[] = new float[9];
			      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			      if (success) {
				      float orientation[] = new float[3];
				      SensorManager.getOrientation(R, orientation);
				      azimut = orientation[0]; // orientation contains: azimut, pitch and roll
			      }
		    }
		    mCustomDrawableView.invalidate();
	  }
	}