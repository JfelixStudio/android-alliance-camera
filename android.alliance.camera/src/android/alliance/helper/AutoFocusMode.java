package android.alliance.helper;

import alliance.camera.R;

public enum AutoFocusMode {
	
	AUTO(R.drawable.bt_autofocus_on_selector),
	OFF(R.drawable.bt_autofocus_off_selector);
	
	public int drawable;
	
	private AutoFocusMode(int resId) {
		this.drawable = resId;
	}
}
