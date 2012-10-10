package android.alliance.orientation;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class YAchseActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
    private float mOrientationValues[] = new float[3];
    private TextView txValue;
    
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yachse);

        txValue = (TextView) findViewById(R.id.yValue);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	 }
	 
	 
	@Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }


	public void onSensorChanged(SensorEvent event) {
		 synchronized (this) {
			 if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                 for (int i=0 ; i<3 ; i++) {
                     mOrientationValues[i] = event.values[i];
                 }
                 
                 txValue.setText(String.valueOf(mOrientationValues[0]));
             } 
        }
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
