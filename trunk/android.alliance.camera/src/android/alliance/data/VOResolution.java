package android.alliance.data;

import android.hardware.Camera.Size;

public class VOResolution {

	private int id;
	private Size size;
	
	public VOResolution(int id, Size size) {
		this.id = id;
		this.size = size;
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
	
	
}
