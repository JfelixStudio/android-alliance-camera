package android.alliance.sensors;

import alliance.sensors.R;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class ProximityActivity extends Activity {

	private TextView tvRaw;
	
	private SensorManager sensorManager;
	private Sensor sensorProximity;
	private SensorEventListener sensorEventListenerProximity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_proximity);
		
		tvRaw = (TextView) findViewById(R.id.tvRaw);
		
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorEventListenerProximity = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				
				tvRaw.setText("0: "+ values[0] + "\n1: " + values[1] + "\n2: " + values[2]);
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		};
	}
	
	@Override
	protected void onResume() {
		sensorManager.registerListener(sensorEventListenerProximity, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(sensorEventListenerProximity);
		super.onPause();
	}
	
}
