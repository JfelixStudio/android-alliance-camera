package android.alliance.camera;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.alliance.camera.R;
import android.alliance.camera.CameraHelper.CameraTarget;
import android.alliance.data.VOContextMenu;
import android.alliance.dialoge.MySpinnerMenuDialog;
public class LayerActivity extends Activity {

	private CameraHelper cameraHelper = CameraHelper.getInstance();
	private OrientationEventListener orientationListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.orientationListener = new OrientationEventListener(this, 1){
	  	    public void onOrientationChanged(int paramAnonymousInt){
	  	    	int angle = Orientation.getInstance().getAngle();
	  			 
	  			 if(angle == 0){
	  				 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	  					
	  			} else if(angle == 90){
	  				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	  			
	  			} else if(angle == 180){
	  				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);	
	  			
	  			} else if(angle == 270){
	  				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
	  			
	  			}
	  	    }
	  	};
	  	
		  	
		String cameraTarget = getIntent().getStringExtra(CameraTarget.CAMERATARGET.getName());
		 
		init(cameraTarget);
		 
	}
	
	private void init(String cameraTarget){
		if(cameraTarget.equals(CameraTarget.AUFLOESUNG.getName())){
			
			String title = getResources().getString(R.string.cameraAufloesung);
			
			final MySpinnerMenuDialog pictureSizeDialog = new MySpinnerMenuDialog(this, R.style.MyStandardAlertDialog, title, cameraHelper.getContextMenuItems(), cameraHelper.getSelectedContextMenuItem());
			pictureSizeDialog.setOnDismissListener(new OnDismissListener() {
		
				@Override
				public void onDismiss(DialogInterface dialog) {
					
					VOContextMenu selectedItem = pictureSizeDialog.getSelectedContextItem();
					
					if(selectedItem != null){
						cameraHelper.setSelectedContextMenuItem(cameraHelper.getContextMenuItems().get(selectedItem.getId()));	
					}
					
					finish();
				}
			});
			pictureSizeDialog.show();
	 }
	}

	
	


}
