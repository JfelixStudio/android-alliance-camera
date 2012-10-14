package android.alliance.camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btBlancCamera;
	private Button btUICamera;
	private Button btAllianceCamera;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		btBlancCamera = (Button) findViewById(R.id.btBlancCamera);
		btBlancCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, BlancCameraActivity.class);
//				intent.putExtra(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_FRONT);
				intent.putExtra(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
				intent.putExtra(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, true);
		        startActivity(intent);
			}
		});
		
		btUICamera = (Button) findViewById(R.id.btUICamera);
		btUICamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UICameraActivity.class);
				intent.putExtra(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_BACK);
				intent.putExtra(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, true);
		        startActivity(intent);
			}
		});
		
		btAllianceCamera = (Button) findViewById(R.id.btAllianceCamera);
		btAllianceCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AllianceCameraActivity.class);
		        startActivity(intent);
			}
		});
	}
}
