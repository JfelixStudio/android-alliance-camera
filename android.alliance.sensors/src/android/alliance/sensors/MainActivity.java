package android.alliance.sensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btSensor;
	private Button btYAchse;
	private Button btCheckSensor;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        btSensor = (Button) findViewById(R.id.btSensor);
	        btSensor.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, SensorActivity.class);
			        startActivity(intent);
				}
			});
	        
	        btYAchse = (Button) findViewById(R.id.btYAchse);
	        btYAchse.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, YAchseActivity.class);
			        startActivity(intent);
				}
			});
	        
	        btCheckSensor = (Button) findViewById(R.id.btCheckSensor);
	        btCheckSensor.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, CheckSensorActivity.class);
			        startActivity(intent);
				}
			});
	 }
}
