package android.alliance.sensors;

import alliance.sensors.R;
import android.app.Application;
import android.widget.TextView;

public class SensorApplication extends Application {

	private static SensorApplication instance;

	private String console = "";
	
	public static SensorApplication getInstance() {
		if(instance == null){
			instance = new SensorApplication();
		}
		
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
	}
	
	public String getConsole() {
		return console;
	}
	
	public void addConsoleLine(String line, TextView tv) {
		console += line + "\n";
		tv.setText(console);
	}
	
	public void clearConsole(TextView tv) {
		console = "";
		tv.setText(console);
	}
}
