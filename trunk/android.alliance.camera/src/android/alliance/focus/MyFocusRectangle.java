package android.alliance.focus;

import android.alliance.camera.R;
import android.alliance.focus.FocusView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MyFocusRectangle extends View implements FocusView {

    private int xActual, yActual;

    public MyFocusRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable(int resid) {
        setBackgroundDrawable(getResources().getDrawable(resid));
    }

    public void showStart() {
        setDrawable(R.drawable.sk_auto_focusing);
    }

    public void showSuccess() {
        setDrawable(R.drawable.sk_auto_focused);
    }

    public void showFail() {
    	setDrawable(R.drawable.sk_auto_fail);
    }

    public void clear() {
        setBackgroundDrawable(null);
    }

    public void setPosition(int x, int y) {
        if (x >= 0 && y >= 0) {
            xActual = x;
            yActual = y;
            redraw();
        }
    }

    public void redraw() {
        int size = getWidth() / 2;
        this.layout(xActual - size, yActual - size, xActual + size, yActual + size);
    }
}

