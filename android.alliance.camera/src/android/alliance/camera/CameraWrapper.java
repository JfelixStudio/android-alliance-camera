package android.alliance.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import alliance.camera.R;
import android.alliance.data.WhiteBalance;
import android.alliance.exceptions.AllianceExceptionType;
import android.alliance.exceptions.OnException;
import android.alliance.focus.MyFocusRectangle;
import android.alliance.helper.AutoFocusHelper;
import android.alliance.helper.AutoFocusMode;
import android.alliance.helper.FlashlightHelper;
import android.alliance.helper.FlashlightHelper.FlashMode;
import android.app.Activity;
import android.graphics.RectF;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class CameraWrapper implements IAllianceCameraListener, OnClickListener, OnException {

	final private Activity ctx;
	private RelativeLayout relativeLayout;
	private RelativeLayout cameraLayout;
	private AllianceCamera allianceCamera;
	
	private ImageView ivShutter; 
	private ImageView ivIso;
	private ImageView ivWhiteBalance;
	private ImageView ivFlash;
	private ImageView ivPreferences;
	private ScrollView scv;
	private View vLineHorizontal;
	
	private ImageView activeMenuImageView = null;
	
	private RelativeLayout.LayoutParams params;
	private SurfaceView surfaceView;
	private FrameLayout flSurface;
	
	private PictureCallback jpegCallback;
	
	public CameraWrapper(final Activity ctx, final RelativeLayout relativeLayout, RelativeLayout.LayoutParams params) {
		this.ctx = ctx;
		this.relativeLayout = relativeLayout;
		
		LayoutInflater layoutInflater = ctx.getLayoutInflater();
		cameraLayout = (RelativeLayout) layoutInflater.inflate(R.layout.camera_wrapper, null);
		
		flSurface = new FrameLayout(ctx);
		
		surfaceView = new SurfaceView(ctx);
		flSurface.addView(surfaceView);
		
		MyFocusRectangle focusRect = new MyFocusRectangle(ctx);
		focusRect.setId(R.id.focus_rectangle);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flSurface.addView(focusRect);
		
		this.params = params;
		relativeLayout.addView(flSurface, params);
		relativeLayout.addView(cameraLayout);
		
		allianceCamera = new AllianceCamera(ctx, surfaceView, CameraInfo.CAMERA_FACING_BACK, false, null, this);
		allianceCamera.setPictureSizeMegapixel(3000000);
		
		FlashMode.FLASH_AUTO.drawable = R.drawable.bt_flashlight_auto_selector;
		FlashMode.FLASH_ON.drawable = R.drawable.bt_flashlight_on_selector;
		FlashMode.FLASH_OFF.drawable = R.drawable.bt_flashlight_off_selector;
		FlashMode.FLASH_TORCH.drawable = R.drawable.bt_flashlight_torch_selector;
		
		FlashlightHelper flashlightHelper = new FlashlightHelper(ctx);
		flashlightHelper.addToSequence(FlashMode.FLASH_AUTO);
		flashlightHelper.addToSequence(FlashMode.FLASH_ON);
		flashlightHelper.addToSequence(FlashMode.FLASH_OFF);
		flashlightHelper.addToSequence(FlashMode.FLASH_TORCH);
		
		allianceCamera.setInitFlashlightHelper(flashlightHelper, -1);
		
		
		AutoFocusHelper autofocusHelper = new AutoFocusHelper(ctx);
		autofocusHelper.addToSequence(AutoFocusMode.AUTO);
		autofocusHelper.addToSequence(AutoFocusMode.OFF);
		autofocusHelper.setStartingMode(AutoFocusMode.OFF);
		allianceCamera.setAutoFocusHelper(autofocusHelper);
		surfaceView.setOnClickListener(this);
		
		
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
				
				allianceCamera.setFilePath(filePath);
				allianceCamera.capture(null, null, CameraWrapper.this.jpegCallback);
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
		
		ivFlash = (ImageView) cameraLayout.findViewById(R.id.ivFlash);
		ivFlash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMenu();
				allianceCamera.nextFlashMode(ivFlash);
			}
		});
		
		ivPreferences = (ImageView) cameraLayout.findViewById(R.id.ivPreferences);
		ivPreferences.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPreferences();
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
		
	}
	
	public void onDestroy() {
		relativeLayout.removeAllViews();
		allianceCamera.releaseCamera();
	}
	
	/**
	 * If a menu is open, close it else return that the camera can get closed
	 * @return
	 */
	public boolean onBackPressed() {
		if(activeMenuImageView != null) {
			hideMenu();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void afterPhotoTaken() {
		allianceCamera.onAfterPhotoTaken();
	}

	private void onWhiteBalance() {
		if(activeMenuImageView != null) {
			if(activeMenuImageView == ivWhiteBalance) {
				hideMenu();
			} else {
				hideMenu();
				showMenuWhiteBalance();
			}
		} else {
			showMenuWhiteBalance();
		}
	}
	
	private void showMenuWhiteBalance() {
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
			
			activeMenuImageView = ivWhiteBalance;
	}
	
	
	private void onISO() {
		if(activeMenuImageView != null) {
			if(activeMenuImageView == ivIso) {
				hideMenu();
			} else {
				hideMenu();
				showMenuISO();
			}
		} else {
			showMenuISO();
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
		
		activeMenuImageView = ivIso;
	}
	
	private void onPreferences() {
		if(activeMenuImageView != null) {
			if(activeMenuImageView == ivPreferences) {
				hideMenu();
			} else {
				hideMenu();
				showPreferences();
			}
		} else {
			showPreferences();
		}
	}
	
	private void showPreferences() {
		scv.setVisibility(View.VISIBLE);
		vLineHorizontal.setVisibility(View.VISIBLE);
		activeMenuImageView = ivPreferences;
		activeMenuImageView.setBackgroundColor(ctx.getResources().getColor(R.color.holo_blue_dark));
		
		LayoutParams layoutParams = scv.getLayoutParams();
		layoutParams.height = relativeLayout.getHeight()/2;
		scv.setLayoutParams(layoutParams);
		
		
	}
	
	private void hideMenu() {
		if(activeMenuImageView != null) {
			scv.setVisibility(View.INVISIBLE);
			scv.removeAllViews();
			vLineHorizontal.setVisibility(View.INVISIBLE);
			activeMenuImageView.setBackgroundColor(ctx.getResources().getColor(R.color.transparent_full));
			activeMenuImageView = null;
		}
	}
	
	public void setPosition(RectF rect) {
		params.width = (int)rect.width();
		params.height = (int)rect.height();
		
		params.leftMargin = (int)rect.left+1;
		params.topMargin = (int)rect.top;

		flSurface.setLayoutParams(params);
//		surfaceView.setLayoutParams(params);
	}
	
	public void setPictureCallback(PictureCallback pictureCallback) {
		this.jpegCallback = pictureCallback;
	}
	
	public void setPictureSize(int width, int height) {
		allianceCamera.setPictureSize(width, height);
	}

	@Override
	public void onClick(View v) {
		if(allianceCamera.autofocusHelper.available) {
			allianceCamera.autofocusHelper.doAutoFocus();
		}
	}

	@Override
	public void onException(Exception exception, String message, AllianceExceptionType type) {
		// TODO Auto-generated method stub
		
	}
}
