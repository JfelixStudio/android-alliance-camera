package android.alliance.camera.start;

import android.alliance.camera.AllianceCamera;
import android.alliance.camera.BlancCameraActivity;
import android.alliance.camera.UICameraActivity;
import android.alliance.camera.CameraWrapperActivity;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btBlancCamera;
	private Button btUICamera;
	private Button btCameraWrapper;
	
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
				intent.putExtra(AllianceCamera.INTENT_KEY_INITIAL_CAMERA_FACING, CameraInfo.CAMERA_FACING_FRONT);
				intent.putExtra(AllianceCamera.INTENT_KEY_USE_ALTERNATIVE_FACING, false);
		        startActivityForResult(intent, 666);
			}
		});
		
		btCameraWrapper = (Button) findViewById(R.id.btCameraWrapper);
		btCameraWrapper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CameraWrapperActivity.class);
		        startActivity(intent);
			}
		});
		
	}
	
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == 0){
			switch(requestCode){
	     		case 666:
	     			if(resultCode == RESULT_OK){
	     				Log.d("ResultCode", "OK");
	     			} else {
	     				Log.d("ResultCode", "DEFAULT");
	     			}
	     			
	     			break;
			}
		}
	}
}
