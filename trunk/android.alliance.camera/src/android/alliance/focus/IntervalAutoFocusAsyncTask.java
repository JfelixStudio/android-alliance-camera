package android.alliance.focus;

import android.os.AsyncTask;

public class IntervalAutoFocusAsyncTask extends AsyncTask<AutoFocus, Void, AutoFocus> {

	private AutoFocus autoFocus;
	
	@Override
	protected AutoFocus doInBackground(AutoFocus... autoFocus) {
		
		this.autoFocus = autoFocus[0];
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
    protected void onPostExecute(AutoFocus autoFocus) {
		this.autoFocus.autoFocus();
    }
	
}