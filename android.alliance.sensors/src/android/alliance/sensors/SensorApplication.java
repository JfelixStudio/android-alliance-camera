package android.alliance.sensors;

import android.app.Application;
import android.widget.TextView;

public class SensorApplication extends Application {

	private static SensorApplication instance;

	private String console = "";
	
	public static SensorApplication getInstance() {
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
