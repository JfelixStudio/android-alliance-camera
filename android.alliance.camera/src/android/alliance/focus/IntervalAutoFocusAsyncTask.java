package android.alliance.focus;

import android.os.AsyncTask;

/**
 * The task can be executed only once (an exception will be thrown if a second execution is attempted.)
 *  
 * @author strangeoptics
 */
public class IntervalAutoFocusAsyncTask extends AsyncTask<AutoFocus, Void, AutoFocus> {

	@Override
	protected AutoFocus doInBackground(AutoFocus... autoFocus) {
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return autoFocus[0];
	}
	
	@Override
    protected void onPostExecute(AutoFocus autoFocus) {
		autoFocus.autoFocus();
    }
	
}