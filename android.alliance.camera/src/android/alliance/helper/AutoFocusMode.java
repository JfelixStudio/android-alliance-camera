package android.alliance.helper;

import alliance.camera.R;

public enum AutoFocusMode {
	
	ON(R.drawable.bt_autofocus_on_selector),
	MANUAL(R.drawable.bt_autofocus_off_selector),
	OFF(R.drawable.bt_autofocus_off_selector);
	
	public int drawable;
	
	private AutoFocusMode(int resId) {
		this.drawable = resId;
	}
}
