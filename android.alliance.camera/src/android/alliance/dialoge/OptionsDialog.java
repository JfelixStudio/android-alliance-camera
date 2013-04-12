package android.alliance.dialoge;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.hardware.Camera;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import alliance.camera.R;
import android.alliance.data.Resolution;
import android.alliance.focus.SensorAutoFocus;
import android.alliance.helper.AutoFocusHelper;
import android.alliance.helper.ResolutionHelper;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class OptionsDialog extends Dialog implements OnClickListener, OnSeekBarChangeListener {
	 
	private TextView txTitle;
	private Context ctx;
	
	private String title;
	private SeekBar autofocusSensibilityBar;
	private Button btSave;
	private Button btAbort;
	private Button btDefault;
	private TextView tvAutoFocusSensibility;
	private AutoFocusHelper autofocusHelper;
	private float actualAutofocusSensibilityValue = 0.00f;
	private Camera camera;
	private int seekbarValue = 200;
	
	public OptionsDialog(Context ctx, int style, AutoFocusHelper autoFocusHelper, Camera camera) {
		super(ctx, style);
		
		this.ctx = ctx;
		this.autofocusHelper = autoFocusHelper;
		this.camera = camera;
	}

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		if(autofocusHelper.THRESHOLD != null){
			actualAutofocusSensibilityValue = autofocusHelper.THRESHOLD * seekbarValue;
		} else {
			actualAutofocusSensibilityValue = SensorAutoFocus.THRESHOLD * seekbarValue;
		}
		
		setContentView(R.layout.optionsdialog);
		
		ImageView backImage = (ImageView) findViewById(R.id.backImage);
		backImage.setOnClickListener(this);
		
		autofocusSensibilityBar = (SeekBar) findViewById(R.id.seekBarAutofocusSensibility);
		autofocusSensibilityBar.setMax(1000);
		autofocusSensibilityBar.setOnSeekBarChangeListener(this);
		autofocusSensibilityBar.setProgress((int) actualAutofocusSensibilityValue);
		
		tvAutoFocusSensibility = (TextView) findViewById(R.id.tvAutoFocusSensibility);
		tvAutoFocusSensibility.setText(ctx.getResources().getString(R.string.autoFocusSensibility) + ": " + String.valueOf(actualAutofocusSensibilityValue));
		
		
		
		btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(this);
		
		btAbort = (Button) findViewById(R.id.btAbort);
		btAbort.setOnClickListener(this);
		
		btDefault = (Button) findViewById(R.id.btSeekbarDefault);
		btDefault.setOnClickListener(this);
   }
   
	
	@Override
	public void onBackPressed() {
		dismiss();
	}


	@Override
	public void onClick(View v) {
		
		if(v == btDefault){
			actualAutofocusSensibilityValue = SensorAutoFocus.THRESHOLD * seekbarValue;
			autofocusSensibilityBar.setProgress((int) actualAutofocusSensibilityValue);
			autofocusHelper.setAutofocusSensibility(null);
			tvAutoFocusSensibility.setText(ctx.getResources().getString(R.string.autoFocusSensibility) + ": " + String.valueOf(SensorAutoFocus.THRESHOLD));
			
		} else if(v == btSave){
			autofocusHelper.setAutofocusSensibility(actualAutofocusSensibilityValue);
			autofocusHelper.initAutoFocus(camera);
			onBackPressed();
		}  else if (v == btAbort){
			onBackPressed();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		actualAutofocusSensibilityValue = (float) progress/seekbarValue;
		
		if(tvAutoFocusSensibility != null){
			tvAutoFocusSensibility.setText(ctx.getResources().getString(R.string.autoFocusSensibility) + ": " + String.valueOf(actualAutofocusSensibilityValue));	
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
