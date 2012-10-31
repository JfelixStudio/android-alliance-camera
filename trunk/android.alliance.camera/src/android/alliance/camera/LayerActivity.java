package android.alliance.camera;

import android.alliance.data.VOResolution;
import android.alliance.dialoge.ResolutionDialog;
import android.alliance.helper.ResolutionHelper;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

public class LayerActivity extends Activity implements IAllianceOrientationChanged {

	private RadioGroup customRadioGroup;
	private ResolutionHelper resolutionHelper = ResolutionHelper.getInstance();
	private AllianceOrientationEventListener orientationListener;
	private TextView txTitle;
	private ImageView backImage;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.resolutiondialog);
		
		orientationListener = new AllianceOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
		
		txTitle = (TextView) findViewById(R.id.title);
		txTitle.setText("Test-Titel");

		backImage = (ImageView) findViewById(R.id.backImage);
		backImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
			
		});
		
		customRadioGroup = (RadioGroup) findViewById(R.id.customRadioGroup);
		
		
		
		for(VOResolution cm : resolutionHelper.lSupportedPictureSizes) {
			
			final RadioButton bt = new RadioButton(this);
			bt.setTextColor(getResources().getColor(R.color.grau));
			bt.setButtonDrawable(getResources().getDrawable(R.drawable.radio_bt_selector));
			bt.setMinWidth(40);
			bt.setTextSize(20);
			bt.setId(cm.getId());
			bt.setText(cm.getSize().width + "x" + cm.getSize().height);
			bt.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					for(VOResolution cm : resolutionHelper.lSupportedPictureSizes) {
						if(cm.getId() == bt.getId()) {
							resolutionHelper.selectedResolution = cm.getSize();
							
							break;
						}
					}
					
					onBackPressed();
				}
				
			});
			
			Size selectedRes = resolutionHelper.selectedResolution;
			if(selectedRes != null && selectedRes == cm.getSize()){
				bt.setChecked(true);
			} else {
				bt.setChecked(false);
			}
			
			
			View v = new View(this);
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradientline));
			v.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			customRadioGroup.addView(bt);
			customRadioGroup.addView(v);
		}
		
		
		orientationListener.addOrientationChangedListeners(this);
		orientationListener.enable();
	}

	@Override
	public void onAllianceOrientationChanged(int orientation, int orientationType, int rotation) {

		orientationHasChanged(rotation);
		
	}
	
	private void orientationHasChanged(float degree) {
			 
		rotateView(txTitle, degree);
		rotateView(backImage, degree);
		rotateView(customRadioGroup, degree);
	}

	private void rotateView(View view, float degree) {
		// check to null, if zoom buttons not initialize
		if(view != null){
			// TODO: 90° kommen von der landscape orientation und sollten dynamisch
			// ausgelesen werden
			Animation an = new RotateAnimation(0.0f, -degree, view.getWidth() / 2, view.getHeight() / 2);

			an.setDuration(0);
			an.setRepeatCount(0);
			an.setFillAfter(true);

			view.startAnimation(an);			
			
			
		}

	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
