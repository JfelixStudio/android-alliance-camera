package android.alliance.focus;

import alliance.camera.autofocus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class MyFocusRectangle extends View implements FocusView {

	public MyFocusRectangle(Context context) {
		super(context);
	}
	
    public MyFocusRectangle(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	private void setDrawable(int resid) {
        
		Drawable bitmapDrawable = getResources().getDrawable(resid);
		
		Bitmap bmp = ((BitmapDrawable) bitmapDrawable).getBitmap();
		
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bmp.getWidth(), bmp.getHeight());
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        
        setLayoutParams(params);
        
        setBackgroundDrawable(bitmapDrawable);
    }

    public void showStart() {
        setDrawable(R.drawable.bt_auto_focusing);
    }

    public void showSuccess() {
        setDrawable(R.drawable.bt_auto_focused);
    }

    public void showFail() {
    	setDrawable(R.drawable.bt_auto_fail);
    }

    public void clear() {
        setBackgroundDrawable(null);
    }
}

