package android.alliance.dialoge;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.hardware.Camera.Size;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import alliance.camera.R;
import android.alliance.data.Resolution;
import android.alliance.helper.ResolutionHelper;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class ResolutionDialog extends Dialog implements OnClickListener{
	 
	private TextView txTitle;
	private Context ctx;
	
	private String title;
	private RadioGroup customRadioGroup;
	private ResolutionHelper resolutionHelper = ResolutionHelper.getInstance();
	
	public ResolutionDialog(Context ctx, int style) {
		super(ctx, style);
		
		this.ctx = ctx;
	}

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.resolutiondialog);
		
		ImageView backImage = (ImageView) findViewById(R.id.backImage);
		backImage.setOnClickListener(this);
		
		customRadioGroup = (RadioGroup) findViewById(R.id.customRadioGroup);
		
		for(Resolution cm : resolutionHelper.supportedPictureSizes) {
			
			final RadioButton bt = new RadioButton(ctx);
			bt.setTextColor(ctx.getResources().getColor(R.color.grau));
			bt.setButtonDrawable(ctx.getResources().getDrawable(R.drawable.bt_radio_selector));
			bt.setTextSize(18);
			bt.setId(cm.getId());
			bt.setText("   " + cm.getSize().width + "x" + cm.getSize().height);
			bt.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					for(Resolution cm : resolutionHelper.supportedPictureSizes) {
						if(cm.getId() == bt.getId()) {
							resolutionHelper.selectedResolution = cm;
							
							break;
						}
					}
					dismiss();
				}
				
			});
			
			if(resolutionHelper.selectedResolution == cm){
				bt.setChecked(true);
			} else {
				bt.setChecked(false);
			}
			
			customRadioGroup.addView(bt);
		}
   }
   
	
	@Override
	public void onBackPressed() {
		dismiss();
	}


	@Override
	public void onClick(View v) {
		onBackPressed();
	}
}
