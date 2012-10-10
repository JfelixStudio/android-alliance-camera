package android.alliance.widgets;

import android.alliance.camera.R;
import android.alliance.helper.Orientation;
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
 
        float paddingleft = (float) getHeight() * 0.15f;
        
        if(angle == 270){
       	 canvas.rotate(90);	
       	 canvas.translate(0 + paddingleft, ((float) -getWidth() * 0.34f)); // kleiner = tiefer
        
        } else if(angle == 180){
       	 canvas.rotate(180);
       	 canvas.translate(-getWidth() + paddingleft, ((float) -getHeight() * 0.37f)); // kleiner = tiefer
       	 
        } else if(angle == 90){
       	 canvas.rotate(-90);
       	 canvas.translate(-getHeight() + paddingleft, ((float) getHeight() * 0.63f)); // größer = tiefer
       	 
        } else if(angle == 0){
       	 canvas.translate(0 + paddingleft, ((float) getHeight() * 0.62f)); // größer = tiefer
        }
        
        getLayout().draw( canvas );
        
        canvas.restore();
   }

}
