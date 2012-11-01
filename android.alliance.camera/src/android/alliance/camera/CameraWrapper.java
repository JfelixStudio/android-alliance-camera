package android.alliance.camera;

import android.app.Activity;
import android.hardware.Camera.CameraInfo;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CameraWrapper {

	private Activity ctx;
	private AllianceCamera allianceCamera;
	
	public CameraWrapper(Activity ctx, ViewGroup viewGroup) {
		this.ctx = ctx;
		
		LayoutInflater layoutInflater = ctx.getLayoutInflater();
		FrameLayout cameraLayout = (FrameLayout) layoutInflater.inflate(R.layout.camera_wrapper, null);
		SurfaceView surfaceView = (SurfaceView) cameraLayout.findViewById(R.id.sv_camera);
		
		
		allianceCamera = new AllianceCamera(ctx, surfaceView, CameraInfo.CAMERA_FACING_BACK, false, null);
	}
}
