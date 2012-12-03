package android.alliance.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import alliance.camera.R;
import android.alliance.helper.CameraUtil;
import android.alliance.helper.Exif;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraWrapperActivity extends Activity {

	private CameraWrapper cameraWrapper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#", "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		RelativeLayout relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		relativeLayout.setBackgroundColor(Color.RED);
		addContentView(relativeLayout, params);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 500);
		layoutParams.leftMargin = 100;
		layoutParams.topMargin = 100;
		cameraWrapper = new CameraWrapper(this, relativeLayout, layoutParams);
		cameraWrapper.setPictureCallback(new JpegCallback());
	}
	
	@Override
	protected void onStart() {
		Log.d("#", "onStart()");
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		Log.d("#", "onRestart()");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		Log.d("#", "onResume()");
		super.onResume();
		cameraWrapper.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("#", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("#", "onStop()");
		super.onStop();
		cameraWrapper.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d("#", "onDestroy()");
		super.onDestroy();
		cameraWrapper.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// check if the camera hase something to go back to
		if(cameraWrapper.onBackPressed()) {
			super.onBackPressed();
		} 
	}
	
	private class JpegCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera cam) {
			Log.d("#", "onPictureTaken()");

			String folderPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamTest/";
			File x = new File(folderPath);
			x.mkdirs();
			String fileName = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";
			File filePath = new File(folderPath, fileName);
			
			// TODO: gibt es eine Device das orientation in den exif-daten
			// speichert? Ja das Samsung Galaxy 10.1n
			/*
			 * The camera driver may set orientation in the EXIF header without
			 * rotating the picture. Or the driver may rotate the picture and
			 * the EXIF thumbnail. If the Jpeg picture is rotated, the
			 * orientation in the EXIF header will be missing or 1 (row #0 is
			 * top and column #0 is left side).
			 * 
			 * 3, 6, 8 
			 */
			
			int orientation = Exif.getOrientation(data);
			Log.d("#", "onPictureTaken().orientation = " + orientation);
			
			if(orientation != 0) {

				Bitmap bmpSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
					
					if(bmpSrc.getWidth()*bmpSrc.getHeight() > 4000000) {
						Toast.makeText(CameraWrapperActivity.this, "image to big", Toast.LENGTH_SHORT).show();
					}
					
					Bitmap bmpRotated = CameraUtil.rotate(bmpSrc, orientation);
					bmpSrc.recycle();
					
					try {
						
						FileOutputStream localFileOutputStream = new FileOutputStream(filePath);
						bmpRotated.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
						
						localFileOutputStream.flush();
						localFileOutputStream.close();
						bmpRotated.recycle();
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
			} else {

				try {
	
					FileOutputStream localFileOutputStream = new FileOutputStream(filePath);
	
					localFileOutputStream.write(data);
					localFileOutputStream.flush();
					localFileOutputStream.close();
	
				} catch (IOException localIOException) {
					// TODO
					Log.e("#",localIOException.getMessage());
				}
			}
			
			cameraWrapper.afterPhotoTaken();
		}
	}
}
