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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CameraWrapper implements IAllianceCameraListener {

	private Activity ctx;
	private ViewGroup viewGroup;
	private FrameLayout cameraLayout;
	private AllianceCamera allianceCamera;
	
	private ImageView ivShutter; 
	
	public CameraWrapper(Activity ctx, ViewGroup viewGroup, RelativeLayout.LayoutParams params) {
		this.ctx = ctx;
		this.viewGroup = viewGroup;
		
		LayoutInflater layoutInflater = ctx.getLayoutInflater();
		cameraLayout = (FrameLayout) layoutInflater.inflate(R.layout.camera_wrapper, null);
		SurfaceView surfaceView = (SurfaceView) cameraLayout.findViewById(R.id.sv_camera);
		viewGroup.addView(cameraLayout, params);
		
		allianceCamera = new AllianceCamera(ctx, surfaceView, CameraInfo.CAMERA_FACING_BACK, false, null);
		
		
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
	}
	
	@Override
	public void onCameraCreated() {
		
	}
	
	public void onResume() {
		allianceCamera.addAllianceCameraListener(this);
	}
	
	public void onStop() {
		viewGroup.removeView(cameraLayout);
		
		allianceCamera.releaseCamera();
	}

	@Override
	public void afterPhotoTaken() {
		
	}
}
