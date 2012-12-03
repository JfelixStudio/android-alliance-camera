package android.alliance.orientation;

import alliance.orientation.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btOrientation;
	private Button btOrientationLandscape;
	private Button btConfigurationChanged;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        btOrientation = (Button) findViewById(R.id.btOrientation);
	        btOrientation.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OrientationApplication.getInstance().clearConsole();
					Intent intent = new Intent(MainActivity.this, OrientationActivity.class);
			        startActivity(intent);
				}
			});
	        
	        btOrientationLandscape = (Button) findViewById(R.id.btOrientationLandscape);
	        btOrientationLandscape.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OrientationApplication.getInstance().clearConsole();
					Intent intent = new Intent(MainActivity.this, OrientationLandscapeActivity.class);
			        startActivity(intent);
				}
			});
	        
	        btConfigurationChanged = (Button) findViewById(R.id.btConfigurationChanged);
	        btConfigurationChanged.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OrientationApplication.getInstance().clearConsole();
					Intent intent = new Intent(MainActivity.this, ConfigurationChangedActivity.class);
			        startActivity(intent);
				}
			});
	 }
}
