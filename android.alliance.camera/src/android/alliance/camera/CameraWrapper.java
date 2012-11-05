package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
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
	private ImageView ivResolution;
	private ScrollView scv;
	
	public CameraWrapper(final Activity ctx, RelativeLayout relativeLayout, RelativeLayout.LayoutParams params) {
		this.ctx = ctx;
		this.relativeLayout = relativeLayout;
		
		LayoutInflater layoutInflater = ctx.getLayoutInflater();
		cameraLayout = (RelativeLayout) layoutInflater.inflate(R.layout.camera_wrapper, null);
		SurfaceView surfaceView = new SurfaceView(ctx);
		
		relativeLayout.addView(surfaceView, params);
		relativeLayout.addView(cameraLayout);
		
		allianceCamera = new AllianceCamera(ctx, surfaceView, CameraInfo.CAMERA_FACING_BACK, false, null);
		
		
		scv = (ScrollView) cameraLayout.findViewById(R.id.scrollView1);
		
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
		
		ivResolution = (ImageView) cameraLayout.findViewById(R.id.ivResolution);
		ivResolution.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(scv.getVisibility() == View.INVISIBLE) {
					scv.setVisibility(View.VISIBLE);
					
					RadioGroup rg = new RadioGroup(ctx);
					scv.addView(rg);
					
					RadioButton rbChecked = null;
					String[] isoValues = allianceCamera.getIsoValues();
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
							allianceCamera.setIsoValue((String) rbc.getText());
						}
					});
					
					rg.check(rbChecked.getId());
					
				} else {
					scv.setVisibility(View.INVISIBLE);
					scv.removeAllViews();
				}
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
}
