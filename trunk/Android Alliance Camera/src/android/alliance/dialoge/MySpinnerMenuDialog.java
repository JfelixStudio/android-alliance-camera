package android.alliance.dialoge;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.alliance.camera.R;
import android.alliance.data.VOContextMenu;
import android.alliance.helper.WidgetScaler;
import android.view.View.OnClickListener;

public class MySpinnerMenuDialog extends Dialog implements OnClickListener{
	 
	private Button btAnlegen;
	private TextView txEingabe;
	private TextView txTitle;
	private Activity parentActivity;
	private ListView listView;
	private List<VOContextMenu> lContextMenuItems;
	private String title;
	private VOContextMenu selectedContextItem = null;
	private RadioGroup customRadioGroup;
	private VOContextMenu preSelected;
	private WidgetScaler ws;
	
	public MySpinnerMenuDialog(Context context, int style, String title, List<VOContextMenu> lContextMenuItems, VOContextMenu preSelected) {
		super(context, style);
		
		this.parentActivity = (Activity) context;
		this.lContextMenuItems = lContextMenuItems;
		this.title = title;
		this.preSelected = preSelected;
		
		ws = WidgetScaler.getInstance(parentActivity);
	}

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.myspinnermenu);
		
		txTitle = (TextView) findViewById(R.id.title);
		txTitle.setLayoutParams(ws.get_dialog_title_ueberschrift_layout());
		txTitle.setPadding(ws.get_dialog_title_ueberschrift_paddingleft(), 0, 0, 0);
		txTitle.setTextSize(ws.get_TextSize());
		txTitle.setText(title);

		TableLayout umrandungAussen = (TableLayout) findViewById(R.id.umrandungAussen);
		umrandungAussen.setLayoutParams(ws.get_mycontextmenu_umrandungAussen());
		
		LinearLayout radarAbstandLinksZumRand = (LinearLayout) findViewById(R.id.radarAbstandLinksZumRand);
		radarAbstandLinksZumRand.setPadding(ws.get_radarAbstandLinksZumRand_spinner(), 0, 0, 0);
		
		ImageView backImage = (ImageView) findViewById(R.id.backImage);
		backImage.setLayoutParams(ws.get_back_layout());
		backImage.setOnClickListener(this);
		
		ImageView radarImage = (ImageView) findViewById(R.id.radarImage);
		radarImage.setLayoutParams(ws.get_radar_layout());
		radarImage.setOnClickListener(this);
		
		customRadioGroup = (RadioGroup) findViewById(R.id.customRadioGroup);
		
		
		
		for(VOContextMenu cm : lContextMenuItems) {
			
			final RadioButton bt = new RadioButton(parentActivity);
			bt.setTextColor(parentActivity.getResources().getColor(R.color.grau));
			bt.setButtonDrawable(parentActivity.getResources().getDrawable(R.drawable.radio_bt_selector));
			bt.setPadding(ws.get_myspinnermenu_radiobutton_paddingLeft(), ws.get_myspinnermenu_radiobutton_paddingTop(), 0, ws.get_myspinnermenu_radiobutton_paddingBottom());
			bt.setMinWidth(ws.get_myspinnermenu_radiobutton_minWidth());
			bt.setTextSize(ws.get_TextSize());
			bt.setId(cm.getId());
			bt.setText(cm.getValue());
			bt.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					for(VOContextMenu cm : lContextMenuItems) {
						if(cm.getId() == bt.getId()) {
							selectedContextItem = cm;
							
							break;
						}
					}
					dismiss();
				}
				
			});
			
			if(preSelected != null && preSelected.getId() == cm.getId()){
				bt.setChecked(true);
			} else {
				bt.setChecked(false);
			}
			
			
			View v = new View(parentActivity);
			v.setBackgroundDrawable(parentActivity.getResources().getDrawable(R.drawable.gradientline));
			v.setLayoutParams(ws.get_listitem_Trennstrich());
			
			customRadioGroup.addView(bt);
			customRadioGroup.addView(v);
		}
   }
   
	
	@Override
	public void onBackPressed() {
		selectedContextItem = null;
		dismiss();
	}

	public VOContextMenu getSelectedContextItem() {
		return selectedContextItem;
	}	
	

	@Override
	public void onClick(View v) {
			
		dismiss();
	}
}
