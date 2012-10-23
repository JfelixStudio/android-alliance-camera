package android.alliance.helper;

import java.math.BigDecimal;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

public class WidgetScaler {

	private static WidgetScaler instance;
	private static Integer targetHeight = null;
	private static Integer targetWidth = null;
	private static BigDecimal density = null;
	private int maxTextSizeListItemHauptelement = 40;
	
	public static WidgetScaler getInstance(){
		if(instance == null){
			instance = new WidgetScaler();
		}
		
		return instance;
	}
	
	public void init(int width, int height){
		targetWidth = width;
		targetHeight = height;
	}
	
	public int get_TextSize(){
		int textSize = (int) (targetHeight * 0.025); // 20
		
		if(textSize > maxTextSizeListItemHauptelement){
			textSize = maxTextSizeListItemHauptelement;
		}
		
		return textSize;
	}
	
	public TableRow.LayoutParams get_listitem_Trennstrich(){
		return new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, (int) (targetHeight * 0.0025));
	}
	
	public FrameLayout.LayoutParams get_camera_fokus_layout(){
		int size = (int) (targetHeight * 0.3);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		return params;
	}
}


