package android.alliance.camera;

import android.alliance.camera.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class TextViewRotate extends TextView{
	
	
	public TextViewRotate(Context context) {
		super(context);
	}

	public TextViewRotate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextViewRotate(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


    @Override
    protected void onDraw(Canvas canvas) {
    	int angle = Orientation.getInstance().getAngle();
    	
   	 TextPaint textPaint = getPaint();
        textPaint.setColor( getCurrentTextColor() );
        textPaint.drawableState = getDrawableState();
        
        canvas.save();
 
        if(angle == 270){
       	 canvas.rotate(90);	
       	 canvas.translate(0, ((float) -getWidth() * 0.37f)); // kleiner = tiefer
        
        } else if(angle == 180){
       	 canvas.rotate(180);
       	 canvas.translate(-getWidth(), ((float) -getHeight() * 0.4f)); // kleiner = tiefer
       	 
        } else if(angle == 90){
       	 canvas.rotate(-90);
       	 canvas.translate(-getHeight(), ((float) getHeight() * 0.6f)); // größer = tiefer
       	 
        } else if(angle == 0){
       	 canvas.translate(0, ((float) getHeight() * 0.55f)); // größer = tiefer
        }
        
        getLayout().draw( canvas );
        
        canvas.restore();
   }

}
