package android.alliance.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.alliance.helper.Exif;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class AllianceCamera implements Callback {

	/**
	 * Intent key to send the initial camera facing. <br>
	 * intent.putExtra(BlancCameraActivity.INTENT_KEY_INITIAL_CAMERA_FACING,
	 * CameraInfo.CAMERA_FACING_FRONT);
	 */
	public static String INTENT_KEY_INITIAL_CAMERA_FACING = "InitialCameraFacing";

	/**
	 * Intent key to indicate if the camera can use an alternative facing
	 * camera in case the desired is not available.
	 */
	public static String INTENT_KEY_USE_ALTERNATIVE_FACING = "UseAlternativeFacing";

	private Context ctx;
	private int cameraId;
	private Camera camera;
	private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	private SurfaceView surfaceView;
	private Parameters parameters;
	private int mOrientation;

	private OrientationEventListener orientationListener;

	/**
	 * CameraInfo.CAMERA_FACING_BACK = 0 <br>
	 * CameraInfo.CAMERA_FACING_FRONT = 1
	 * AllianceCamera.CAMERA_FACING_BACK_OR_FRONT
	 */
	private Integer cameraFacing = null;

	private boolean useAlternativeFacing = false;

	public AllianceCamera(Context ctx, SurfaceView surfaceView, int cameraFacing, boolean useAlternativeFacing) {
		this.ctx = ctx;
		this.surfaceView = surfaceView;
		this.cameraFacing = cameraFacing;
		this.useAlternativeFacing = useAlternativeFacing;

		surfaceView.getHolder().addCallback(this);

		/*
		 * deprecated setting, but required on Android versions prior to 3.0
		 * source: http://developer.android.com/guide/topics/media/camera.html
		 */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		orientationListener = new AllianceOrientationEventListener(ctx, SensorManager.SENSOR_DELAY_NORMAL);
	}

	// SurfaceHolder.Callback ////////////////////////////////

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("#", "surfaceCreated()");
		initCamera(holder);
		initCameraPreferences();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d("#", "surfaceChanged(format=" + format + ", width=" + width + ", height=" + height + ")");
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("#", "surfaceDestroyed()");
		camRelease();
	}

	// remaining methods ///////////////////////////////////////////////////

	/**
	 * 
	 * @param holder
	 */
	private void initCamera(SurfaceHolder holder) {

		try {

			// Dieser Code wird unten ersetzt
			camera = openCamera(cameraFacing);
			if (camera == null && useAlternativeFacing) {
				switch (cameraFacing) {
				case CameraInfo.CAMERA_FACING_BACK:
					camera = openCamera(cameraInfo.CAMERA_FACING_FRONT);
					break;
				case CameraInfo.CAMERA_FACING_FRONT:
					camera = openCamera(cameraInfo.CAMERA_FACING_BACK);
					break;
				}
			}

			if (camera != null) {
				camera.setPreviewDisplay(holder);

			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			camRelease();

			Log.e("#", "Camera failed to open: " + e.getLocalizedMessage());
			Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
	}

	/**
	 * Opens a camera for the desired facing
	 * 
	 * @param desiredFacing
	 * @return initialized camera or null
	 */
	private Camera openCamera(int desiredFacing) {
		Camera cam = null;

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == desiredFacing) {
				cam = Camera.open(camIdx);
				cameraId = camIdx;
				break;
			}
		}
		return cam;
	}

	private void initCameraPreferences() {
		if (camera != null) {

			camera.stopPreview();

			parameters = camera.getParameters();

			parameters.setPictureFormat(ImageFormat.JPEG);

			// Setze BestPreviewSize
			// display.width is deprecated was für Alternativen gibt es? die
			// View die als Zeichenfläche dient
			int widthSurface = surfaceView.getWidth();
			int heightSurface = surfaceView.getHeight();

			Size optimalPreviewSize = getBestPreviewSize(widthSurface, heightSurface, parameters.getSupportedPreviewSizes());

			if (optimalPreviewSize != null) {
				parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
			}

			// Setze BestPictureSize
			// Init Autofocus

			camera.setParameters(parameters);

			camera.startPreview();
		}
	}

	public void camRelease() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release(); // Speicher freigeben ? wieso speicher freigeben
			camera = null;
		}
	}

	/**
	 * good source: http://www.java2s.com/Code/Android/Hardware/
	 * Gettheoptimalpreviewsizeforthegivenscreensize.htm
	 * 
	 * @param width
	 * @param height
	 * @param supportedPreviewSizes
	 * @return
	 */
	private Size getBestPreviewSize(int width, int height, List<Size> supportedPreviewSizes) {
		double sourceRatio;

		if (width < height) {
			sourceRatio = (double) width / height;
		} else {
			sourceRatio = (double) height / width;
		}

		int index = 0;
		double lastRatioToCheck = 0.0d;

		for (Size size : supportedPreviewSizes) {

			double targetRatio = 0.0d;

			if (size.width > size.height) {
				sourceRatio = (double) size.width / size.height;
			} else {
				sourceRatio = (double) size.height / size.width;
			}

			if (lastRatioToCheck == 0.0d) {
				lastRatioToCheck = targetRatio;
				index = 0;

			} else {
				// Because of Math.ab there gonves No negative ratio values
				double chkLastTarget = Math.abs(lastRatioToCheck);
				double chkCurrentTarget = Math.abs(targetRatio);

				double chkSource = Math.abs(sourceRatio);

				if (Math.abs(chkSource - chkCurrentTarget) < Math.abs(chkSource - chkLastTarget)) {
					lastRatioToCheck = targetRatio;
					index = supportedPreviewSizes.indexOf(targetRatio);
				}
			}
		}

		return supportedPreviewSizes.get(index);
	}

	/**
	 * Captures the image. If the camera is focusing nothing happens. If the
	 * last focus is more than ~10 seconds left, focus is triggered
	 */
	public void capture() {

		// if focused otherwise focus

		camera.takePicture(null, null, new PhotoCallback());
	}

	private class PhotoCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("#", "onPictureTaken()");
			
			// TODO: gibt es eine Device das orientation in den exif-daten speichert?
			/*
			 * The camera driver may set orientation in the EXIF header without
			 * rotating the picture. Or the driver may rotate the picture and
			 * the EXIF thumbnail. If the Jpeg picture is rotated, the
			 * orientation in the EXIF header will be missing or 1 (row #0 is
			 * top and column #0 is left side).
			 */
			int orientation = Exif.getOrientation(data);
			Log.d("#", "onPictureTaken().orientation = " + orientation);

			try {

				String str = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg";

				File localFile1 = new File(Environment.getExternalStorageDirectory(), "/CamTest/");
				localFile1.mkdirs();

				File localFile2 = new File(localFile1, str);

				FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);

				localFileOutputStream.write(data);
				localFileOutputStream.flush();
				localFileOutputStream.close();

				camera.startPreview();
//				startPreview();

			} catch (IOException localIOException) {
			}
		}

	}

	/**
	 * Creates a new OrientationEventListener.
	 * 
	 * @param context
	 *            for the OrientationEventListener.
	 * @param rate
	 *            at which sensor events are processed (see also
	 *            {@link android.hardware.SensorManager SensorManager}). Use the
	 *            default value of
	 *            {@link android.hardware.SensorManager#SENSOR_DELAY_NORMAL
	 *            SENSOR_DELAY_NORMAL} for simple screen orientation change
	 *            detection.
	 */
	private class AllianceOrientationEventListener extends OrientationEventListener {

		public AllianceOrientationEventListener(Context context, int rate) {
			super(context, rate);
		}

		/*
		 * Called when the orientation of the device has changed. Orientation
		 * parameter is in degrees, ranging from 0 to 359. 0 degrees when the
		 * device is oriented in its natural position.
		 */
		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation == ORIENTATION_UNKNOWN)
				return;

			orientation = (orientation + 45) / 90 * 90;

			if (mOrientation != orientation) {
				Log.d("#", "AllianceOrientationEventListener.orientation = " + orientation);

				mOrientation = orientation;

				// TODO: cameraId muss noch belegt werden
				CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(cameraId, info);

				int rotation = 0;
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else { // back-facing camera
					rotation = (info.orientation + orientation) % 360;
				}
				Log.d("#", "AllianceOrientationEventListener.rotation = " + rotation);

				Parameters localParameters = camera.getParameters();
				/*
				 * Sets the rotation angle in degrees relative to the
				 * orientation of the camera. This affects the pictures returned
				 * from JPEG android.hardware.Camera.PictureCallback.
				 */
				localParameters.setRotation(rotation);
				camera.setParameters(localParameters);
			}
		}
	}

}
