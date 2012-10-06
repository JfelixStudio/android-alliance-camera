package android.alliance.orientation;

import android.app.Application;
import android.widget.TextView;

public class OrientationApplication extends Application {

	private static OrientationApplication instance;

	private String console = "";
	
	public static OrientationApplication getInstance() {
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
	
	public void clearConsole() {
		console = "";
	}
}
