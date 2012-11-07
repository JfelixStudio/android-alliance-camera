package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.alliance.data.WhiteBalance;
import android.app.Activity;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class CameraWrapper implements IAllianceCameraListener {

	final private Activity ctx;
	private RelativeLayout relativeLayout;
	private RelativeLayout cameraLayout;
	private AllianceCamera allianceCamera;
	
	private ImageView ivShutter; 
	private ImageView ivIso;
	private ImageView ivWhiteBalance;
	private ScrollView scv;
	private View vLineHorizontal;
	
	public CameraWrapper(final Activity ctx, final RelativeLayout relativeLayout, RelativeLayout.LayoutParams params) {
		this.ctx = ctx;
		this.relativeLayout = relativeLayout;
		
		LayoutInflater layoutInflater = ctx.getLayoutInflater();
		cameraLayout = (RelativeLayout) layoutInflater.inflate(R.layout.camera_wrapper, null);
		SurfaceView surfaceView = new SurfaceView(ctx);
		
		relativeLayout.addView(surfaceView, params);
		relativeLayout.addView(cameraLayout);
		
		allianceCamera = new AllianceCamera(ctx, surfaceView, CameraInfo.CAMERA_FACING_BACK, false, null);
		
		
		scv = (ScrollView) cameraLayout.findViewById(R.id.scrollView1);
		vLineHorizontal = cameraLayout.findViewById(R.id.line_horizontal);
		
		ivShutter = (ImageView) cameraLayout.findViewById(R.id.ibShutter);
		ivShutter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String folderPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamTest/";
				File x = new File(folderPath);
				x.mkdirs();
				String fileName = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";
				File filePath = new File(folderPath, fileName);
				
				allianceCamera.setFilePaht(filePath);
				allianceCamera.capture();
			}
		});
		
		ivIso = (ImageView) cameraLayout.findViewById(R.id.ivIso);
		ivIso.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onISO();
			}
		});
		
		ivWhiteBalance = (ImageView) cameraLayout.findViewById(R.id.ivWhiteBalance);
		ivWhiteBalance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onWhiteBalance();
			}
		});
	}
	
	@Override
	public void onCameraCreated() {
		
	}
	
	public void onResume() {
		allianceCamera.addAllianceCameraListener(this);
	}
	
	public void onStop() {
//		relativeLayout.removeView(cameraLayout);
		relativeLayout.removeAllViews();
		
		allianceCamera.releaseCamera();
	}

	@Override
	public void afterPhotoTaken() {
		
	}

	
	private void onWhiteBalance() {
		if(scv.getVisibility() == View.INVISIBLE) {
			scv.setVisibility(View.VISIBLE);
			vLineHorizontal.setVisibility(View.VISIBLE);
			ivWhiteBalance.setBackgroundColor(ctx.getResources().getColor(R.color.holo_blue_dark));
			
			LayoutParams layoutParams = scv.getLayoutParams();
			layoutParams.height = relativeLayout.getHeight()/2;
			scv.setLayoutParams(layoutParams);
			
			RadioGroup rg = new RadioGroup(ctx);
			scv.addView(rg);
			
			RadioButton rbChecked = null;
			String[] whiteBalanceValues = allianceCamera.getWhiteBalanceValues();
			if(whiteBalanceValues.length == 1) {
				whiteBalanceValues = new String[] {"auto", "incandescent", "fluorescent", "daylight", "cloudy-daylight"};
			}
			
			for(String whiteBalance : whiteBalanceValues) {
				
				RadioButton rb = new RadioButton(ctx);
				rb.setText(whiteBalance);
				if(whiteBalance.equals(allianceCamera.getWhiteBalance())) {
					rbChecked = rb;
				}
				
				rg.addView(rb);
			}
			rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					RadioButton rbc = (RadioButton) group.findViewById(checkedId);
					allianceCamera.setWhiteBalance((String) rbc.getText());
					
					WhiteBalance whiteBalance = WhiteBalance.getWhiteBalance((String)rbc.getText());
					ivWhiteBalance.setImageResource(whiteBalance.icon);
				}
			});
			
			if(rbChecked != null) { // check for emulator
				rg.check(rbChecked.getId());
			}
		} else {
			hideMenu(ivWhiteBalance);
		}
		
	}
	
	
	private void onISO() {
		if(scv.getVisibility() == View.INVISIBLE) {
			showMenuISO();
		} else {
			hideMenu(ivIso);
		}
	}
	
	private void showMenuISO() {
		scv.setVisibility(View.VISIBLE);
		vLineHorizontal.setVisibility(View.VISIBLE);
		ivIso.setBackgroundColor(ctx.getResources().getColor(R.color.holo_blue_dark));
		
		LayoutParams layoutParams = scv.getLayoutParams();
		layoutParams.height = relativeLayout.getHeight()/2;
		scv.setLayoutParams(layoutParams);
		
		RadioGroup rg = new RadioGroup(ctx);
		scv.addView(rg);
		
		RadioButton rbChecked = null;
		String[] isoValues = allianceCamera.getIsoValues();
		if(isoValues.length == 1) {
			isoValues = new String[] {"auto", "ISO100", "ISO200", "ISO400", "ISO800", "ISO1600", "ISO3200", "ISO6400", "ISO12800"};
		}
		
		for(String isoValue : isoValues) {
			RadioButton rb = new RadioButton(ctx);
			rb.setText(isoValue);
			if(isoValue.equals(allianceCamera.getIsoValue())) {
				rbChecked = rb;
			}
			
			rg.addView(rb);
		}
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rbc = (RadioButton) group.findViewById(checkedId);
				allianceCamera.setIso((String) rbc.getText());
			}
		});
		
		if(rbChecked != null) { // check for emulator
			rg.check(rbChecked.getId());
		}
	}
	
	private void hideMenu(View view) {
		scv.setVisibility(View.INVISIBLE);
		scv.removeAllViews();
		vLineHorizontal.setVisibility(View.INVISIBLE);
		view.setBackgroundColor(ctx.getResources().getColor(R.color.transparent_full));
	}
}
