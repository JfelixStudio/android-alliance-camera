package android.alliance.sensors;

import android.alliance.sensors.average.IAverage;
import android.alliance.sensors.average.MovingAverage;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SensorActivity extends Activity {

	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;
	
	private TextView tvXS;
	private TextView tvYS;
	private TextView tvZS;
	
	private TextView tvConsole;

	private SensorManager sensorManager;
	private Sensor sensorAccel;
	private SensorEventListener sensorEventListenerAccel;
	
	private float[] valuesAccelSmooth = new float[3];
	private float[] valuesDelta = new float[3];
	private float[] valuesOldPeak = new float[3];
	
	private IAverage movingAverage = new MovingAverage(5);

	static final float TRESHOLD = 1.5f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);

		tvX = (TextView) findViewById(R.id.tvX);
		tvY = (TextView) findViewById(R.id.tvY);
		tvZ = (TextView) findViewById(R.id.tvZ);
		
		tvXS = (TextView) findViewById(R.id.tvXS);
		tvYS = (TextView) findViewById(R.id.tvYS);
		tvZS = (TextView) findViewById(R.id.tvZS);
		
		tvConsole = (TextView) findViewById(R.id.tvConsole);
		tvConsole.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				SensorApplication.getInstance().clearConsole(tvConsole);
				return true;
			}
		});

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorEventListenerAccel = new SensorEventListener() {

			int max = 0;
			
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				
				tvX.setText(Float.toString(values[0]));
				tvY.setText(Float.toString(values[1]));
				tvZ.setText(Float.toString(values[2]));
				
				if(max == 2) {
					max = 0;
					
					movingAverage.getAverage(values, valuesAccelSmooth);
					
					tvXS.setText(Float.toString(valuesAccelSmooth[0]));
					tvYS.setText(Float.toString(valuesAccelSmooth[1]));
					tvZS.setText(Float.toString(valuesAccelSmooth[2]));
					
					delta(valuesOldPeak, valuesAccelSmooth, valuesDelta);
					
					if(valuesDelta[0] > TRESHOLD || valuesDelta[1] > TRESHOLD || valuesDelta[2] > TRESHOLD) {
						SensorApplication.getInstance().addConsoleLine("dx:" + valuesDelta[0] + "  dy:" + valuesDelta[1] + "  dz:" + valuesDelta[2], tvConsole);
						valuesOldPeak = valuesAccelSmooth.clone();
					}
				} else {
					max++;
				}
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		
	}
	
	@Override
	protected void onResume() {
		sensorManager.registerListener(sensorEventListenerAccel, sensorAccel, SensorManager.SENSOR_DELAY_UI);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(sensorEventListenerAccel);
		super.onPause();
	}
	
	private void delta(float[] a, float[] b, float[] delta) {
		for ( int i=0; i<a.length; i++ ) {
			delta[i] = Math.abs(a[i] - b[i]);
		}
	}
}
