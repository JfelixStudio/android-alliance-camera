package android.alliance.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotateHelper {

	/**
	 * Rotates a bitmap by some degrees. The bmpSrc stays untouched and a new rotated
	 * Bitmap gets created.
	 * @param bmpSrc
	 * @param degrees	new rotated Bitmap
	 * @return
	 */
	public static Bitmap rotate(Bitmap bmpSrc, int degrees) {
		int w = bmpSrc.getWidth();
		int h = bmpSrc.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(degrees);
		Bitmap bmpTrg = Bitmap.createBitmap(bmpSrc, 0, 0, w, h, mtx, true);
		return bmpTrg;
	}
}
