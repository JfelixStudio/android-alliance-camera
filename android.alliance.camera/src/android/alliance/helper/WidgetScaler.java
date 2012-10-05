package android.alliance.helper;

import java.math.BigDecimal;

import android.alliance.camera.Orientation;
import android.alliance.helper.ScreenSizeHelper;
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
	private static Integer displayHeight = null;
	private static BigDecimal density = null;
	private int maxTextSizeListItemHauptelement = 40;
	private int maxTextSizeListItemSubelement = 30;
	private int maxImageSizeListItemSubPicture = 38;
	private int maxDialogTitleTextSize = 40;
	
	public static WidgetScaler getInstance(Activity activity){
		if(instance == null){
			instance = new WidgetScaler();
		}
		
		if(displayHeight == null){
			displayHeight = ScreenSizeHelper.getInstance().getDisplayHeight(activity);
			density = new BigDecimal(ScreenSizeHelper.getInstance().getDensity());
		}
		
		return instance;
	}
	
	
	
	
	
	/**
	 * main_spinner_listitem.xml
	 */
	public LinearLayout.LayoutParams get_mainspinner_listitem_layout(){
		
		 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		 params.leftMargin = (int) (displayHeight * 0.00625);  // 5
		 params.topMargin = (int) (displayHeight * 0.0013); //1
		 
	     return params;
	}
	
	
	

	
	
	
	
	
	/**
	 * myspinnermenu_listitem.xml
	 */
	public int get_myspinnermenu_radiobutton_paddingTop(){
		return (int) (displayHeight * 0.01625);
	}

	public int get_myspinnermenu_radiobutton_paddingBottom(){
		return (int) (displayHeight * 0.01625);
	}
	
	public int get_myspinnermenu_radiobutton_minWidth(){
		return (int) (displayHeight * 0.3125);
	}

	public int get_myspinnermenu_radiobutton_paddingLeft(){
		
		int p = (int) (displayHeight * 0.075); 
		
		if(p > 80){
			p = 80;
		}

		return p; 
	}
	/**
	 ********************************************************************************************** 
	 */
	
	
	
	
	
	/**
	 * mycontextmenu.xml
	 * myspinnermenu.xml
	 * mystandardalertdialog.xml
	 * mystandardprogessdialog.xml
	 * teilenexportdialog.xml
	 */
	public LinearLayout.LayoutParams get_mycontextmenu_umrandungAussen(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int margins = (int) (displayHeight * 0.004); // 2
		params.leftMargin = margins;
		params.rightMargin = margins;
		params.topMargin = margins;
		params.bottomMargin = margins;
		return params;
	}
	
	public int get_radarAbstandLinksZumRand_spinner(){
		return (int) (displayHeight * 0.0125);
	}

	public int get_radarAbstandLinksZumRand_dialog(){
		return (int) (displayHeight * 0.025);
	}

	
	public LinearLayout.LayoutParams get_radar_layout(){
		int imgSize = (int) (displayHeight * 0.07);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
		params.gravity = Gravity.CENTER_VERTICAL;
		return params;
	}
	
	public LinearLayout.LayoutParams get_back_layout(){
		int imgSize = (int) (displayHeight * 0.03);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
		params.gravity = Gravity.CENTER_VERTICAL;
		return params;
	}
	
	public LinearLayout.LayoutParams get_dialog_title_ueberschrift_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
		params.rightMargin = (int) (displayHeight * 0.02);
		params.topMargin = (int) (displayHeight * 0.02);
		params.bottomMargin = (int) (displayHeight * 0.02);
		return params;
	}
	
	public int get_dialog_title_ueberschrift_paddingleft(){
		return (int) (displayHeight * 0.0125);
	}
	

	public TableRow.LayoutParams get_dialog_layoutMitte(){
		TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
		params.rightMargin = (int) (displayHeight * 0.029);
		params.leftMargin = (int) (displayHeight * 0.029);
		params.bottomMargin = (int) (displayHeight * 0.0125);
		params.topMargin = (int) (displayHeight * 0.0025);
		return params;
	}
	
	public LinearLayout.LayoutParams get_alertdialog_text_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) (displayHeight * 0.014);
		params.bottomMargin = (int) (displayHeight * 0.014);
		return params;
	}
	
	public int get_alertdialog_text_padding(){
		return (int) (displayHeight * 0.015);
	}
	
	public TableRow.LayoutParams get_dialog_button_layout(){
		TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.rightMargin = (int) (displayHeight * 0.029);
		params.leftMargin = (int) (displayHeight * 0.029);
		params.bottomMargin = (int) (displayHeight * 0.0125);
		return params;
	}
	
	public LinearLayout.LayoutParams get_inputdialog_editText_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.LEFT;
		params.rightMargin = (int) (displayHeight * 0.029);
		params.leftMargin = (int) (displayHeight * 0.029);
		return params;
	}
	
	
	public LinearLayout.LayoutParams get_inputdialog_button_left_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		return params;
	}
	
	public LinearLayout.LayoutParams get_inputdialog_button_right_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = - (int) (displayHeight * 0.0025);
		params.weight = 1;
		return params;
	}
	
	public int get_inputdialog_button_paddings(){
		return (int) (displayHeight * 0.015); // 12
	}
	
	public int get_button_TextSize(){
		int textSize = (int) (displayHeight * 0.02); // 16
		
		if(textSize > maxTextSizeListItemHauptelement){
			textSize = maxTextSizeListItemHauptelement;
		}
		
		return textSize;
	}
	
	public int get_progressbar_paddings(){
		return (int) (displayHeight * 0.01875);
	}
	
	public LinearLayout.LayoutParams get_progressDialog_text_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) (displayHeight * 0.05);
		params.bottomMargin = (int) (displayHeight * 0.05);
		return params;
	}
	
	public int get_progressDialog_text_paddingRight(){
		return (int) (displayHeight * 0.019);
	}
	
	public LinearLayout.LayoutParams get_exportdialog_text_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) (displayHeight * 0.06);
		params.bottomMargin = (int) (displayHeight * 0.06);
		return params;
	}
	
	public int get_exportdialog_paddings(){
		return (int) (displayHeight * 0.013);
	}
	
	public LinearLayout.LayoutParams get_contextMenuListItem_layout(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) (displayHeight * 0.013);
		params.rightMargin = (int) (displayHeight * 0.013);
		params.bottomMargin = (int) (displayHeight * 0.0165);
		params.topMargin = (int) (displayHeight * 0.0165);
		return params;
	}
	
	public int get_contextMenuListItem_minWidth(){
		return (int) (displayHeight * 0.4); // 250
	}
	
	/**
	 ********************************************************************************************** 
	 */
	
	public int get_TextSize(){
		int textSize = (int) (displayHeight * 0.025); // 20
		
		if(textSize > maxTextSizeListItemHauptelement){
			textSize = maxTextSizeListItemHauptelement;
		}
		
		return textSize;
	}
	
	public TableRow.LayoutParams get_listitem_Trennstrich(){
		return new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, (int) (displayHeight * 0.0025));
	}
	
	
	
	
	
	
	
	
	public FrameLayout.LayoutParams get_camera_shutterbutton_layout(){
		int size = (int) (displayHeight * 0.15);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		
		int angle = Orientation.getInstance().getAngle();
		
		if(angle == 0){
			params.gravity = Gravity.BOTTOM | Gravity.CENTER;
			
		} else if(angle == 90){
			params.gravity = Gravity.RIGHT | Gravity.CENTER;
		
		} else if(angle == 180){
			params.gravity = Gravity.TOP | Gravity.CENTER;
		
		} else if(angle == 270){
			params.gravity = Gravity.LEFT | Gravity.CENTER;
		}
		
		return params;
	}

	public FrameLayout.LayoutParams get_camera_flashlight_layout(){
		int size = (int) (displayHeight * 0.1);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		
		int angle = Orientation.getInstance().getAngle();
		
		if(angle == 0){
			params.gravity = Gravity.BOTTOM | Gravity.LEFT;
			
		} else if(angle == 90){
			params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		
		} else if(angle == 180){
			params.gravity = Gravity.TOP | Gravity.RIGHT;
		
		} else if(angle == 270){
			params.gravity = Gravity.LEFT | Gravity.TOP;
		
		}
		
		return params;
	}
	
	public FrameLayout.LayoutParams get_camera_zoom_in_layout(){
		int size = (int) (displayHeight * 0.1);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		
		int angle = Orientation.getInstance().getAngle();
		
		if(angle == 0){
			params.gravity = Gravity.TOP | Gravity.RIGHT;
			
		} else if(angle == 90){
			params.gravity = Gravity.LEFT | Gravity.TOP;
		
		} else if(angle == 180){
			params.gravity = Gravity.BOTTOM | Gravity.LEFT;
		
		} else if(angle == 270){
			params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		
		}
		
		return params;
	}
	
	public FrameLayout.LayoutParams get_camera_zoom_out_layout(){
		int size = (int) (displayHeight * 0.1);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		
		int angle = Orientation.getInstance().getAngle();
		
		if(angle == 0){
			params.gravity = Gravity.TOP | Gravity.LEFT;
			
		} else if(angle == 90){
			params.gravity = Gravity.BOTTOM | Gravity.LEFT;
		
		} else if(angle == 180){
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		
		} else if(angle == 270){
			params.gravity = Gravity.RIGHT | Gravity.TOP;
		
		}
		
		return params;
	}
	
	public FrameLayout.LayoutParams get_camera_aufloesung_layout(){
		
		int angle = Orientation.getInstance().getAngle();
		
		int width = (int) (displayHeight * 0.2);
		int height = (int) (displayHeight * 0.2);
		
		int newWidth = 0;
		int newHeight = 0;
		
		if(angle == 0 || angle == 180){
			newWidth = width;
			newHeight = height;
		
		} else if(angle == 90 || angle == 270){
			newWidth = height;
			newHeight = width;
		}
		
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(newWidth, newHeight);

		if(angle == 0){
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			
		} else if(angle == 90){
			params.gravity = Gravity.RIGHT | Gravity.TOP;
		
		} else if(angle == 180){
			params.gravity = Gravity.TOP | Gravity.LEFT;
		
		} else if(angle == 270){
			params.gravity = Gravity.LEFT | Gravity.BOTTOM;
		
		}

				
		return params;
	}


	public FrameLayout.LayoutParams get_camera_fokus_layout(){
		int size = (int) (displayHeight * 0.3);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		return params;
	}
}


