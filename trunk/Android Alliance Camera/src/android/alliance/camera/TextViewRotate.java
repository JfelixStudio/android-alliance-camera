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
//    	setGravity(Gravity.CENTER);

    	int angle = Orientation.getInstance().getAngle();
    	
    	
    	
    	 TextPaint textPaint = getPaint();
         textPaint.setColor( getCurrentTextColor() );
         textPaint.drawableState = getDrawableState();
         
         
         canvas.save();
  
         
//    	 Log.d("#", String.valueOf(angle));
    	 
         
         
         if(angle == 270){
        	 canvas.rotate(90);
        	 canvas.translate(0, -getWidth());
         
         } else if(angle == 180){
//        	 canvas.translate(0,  getHeight());
//        	 
        	 canvas.rotate(180);
        	 canvas.translate(-getWidth(), -getHeight());
        	 
         } else if(angle == 90){
        	 canvas.rotate(-90);
        	 canvas.translate(-getHeight(), 0);
        	 
         } else if(angle == 0){
//        	 canvas.translate(0,  getHeight());
//        	 canvas.rotate(0);
//        	 canvas.translate( getHeight(), 0 );
        	 
//        	 
         }
//         canvas.translate( getCompoundPaddingLeft(), getExtendedPaddingTop() );
  
         getLayout().draw( canvas );
         
         Paint p = new Paint();
         p.setColor(getResources().getColor(R.color.android_green));
         canvas.drawLine(5, 5, 400, 5, p);
         
         canvas.restore();
    }
    
    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ){
        super.onMeasure( heightMeasureSpec, widthMeasureSpec );
        
//        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

}
