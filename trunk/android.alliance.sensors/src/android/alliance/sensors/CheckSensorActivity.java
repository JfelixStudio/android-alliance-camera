package android.alliance.sensors;

import java.util.List;

import alliance.sensors.R;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CheckSensorActivity extends Activity {

	private Context ctx;
	private TextView tvSensorPresent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		Log.d("#", "onCreate()");

		setContentView(R.layout.activity_check_sensor);
		tvSensorPresent = (TextView) findViewById(R.id.tvSensorPresent);
		
		String sensors = "";
		
		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_ACCELEROMETER, "TYPE_ACCELEROMETER", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_MAGNETIC_FIELD, "TYPE_MAGNETIC_FIELD", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_GRAVITY, "TYPE_GRAVITY", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_PROXIMITY, "TYPE_PROXIMITY", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_GYROSCOPE, "TYPE_GYROSCOPE", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE, "TYPE_AMBIENT_TEMPERATURE", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_LIGHT, "TYPE_LIGHT", sensors);
		sensors = checkAvailabilityOfSensorType(sensorManager, Sensor.TYPE_ROTATION_VECTOR, "TYPE_ROTATION_VECTOR", sensors);

		tvSensorPresent.setText(sensors);
		
		System.out.println();
	}

	@Override
	protected void onStart() {
		Log.d("#", "onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.d("#", "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("#", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
	}
	
	private String checkAvailabilityOfSensorType(SensorManager sensorManager, int sensorType, String name, String text) {
		List<Sensor> sensorListMagneticField = sensorManager.getSensorList(sensorType);
		if(sensorListMagneticField.size() > 0) {
			text += name + ": present\n";
		} else {
			text += name+ ": absent\n";
		}
		return text;
	}

}
