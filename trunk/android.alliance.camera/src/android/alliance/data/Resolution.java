package android.alliance.data;

import android.hardware.Camera.Size;

public class Resolution {

	public int id;
	public Size size;
	public int megapixel;
	
	public Resolution(int id, Size size, int megapixel) {
		this.id = id;
		this.size = size;
		this.megapixel = megapixel;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public int getMegapixel() {
		return megapixel;
	}
	public void setMegapixel(int megapixel) {
		this.megapixel = megapixel;
	}
	
	
}
