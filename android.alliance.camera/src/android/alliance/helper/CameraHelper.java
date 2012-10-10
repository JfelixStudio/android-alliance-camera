package android.alliance.helper;

import java.util.ArrayList;
import java.util.List;

import android.alliance.camera.R;
import android.alliance.data.VOContextMenu;

public class CameraHelper {

	private static CameraHelper instance;
	
	private List<VOContextMenu> lContextMenuItems = null;
	private VOContextMenu selectedContextMenuItem = null;
	
	public static CameraHelper getInstance(){
		if(instance == null){
			instance = new CameraHelper();
		}
		return instance;
	}
	
	public void addContextMenuItem(VOContextMenu item){
		if(lContextMenuItems == null){
			lContextMenuItems = new ArrayList<VOContextMenu>();
		}
		
		lContextMenuItems.add(item);
	}
	
	public List<VOContextMenu> getContextMenuItems(){
		return lContextMenuItems;
	}
	
	public void setSelectedContextMenuItem(VOContextMenu item){
		selectedContextMenuItem = item;
	}
	
	public VOContextMenu getSelectedContextMenuItem(){
		return selectedContextMenuItem;
	}
	
	
	public enum CameraTarget {
		CAMERATARGET(new Integer(0), "CameraTarget"),
		AUFLOESUNG(new Integer(1), "Aufloesung");
		
		String name;
		Integer id;
		
		private CameraTarget(Integer id, String name){
			this.name = name;
			this.id = id;
		}
		
		public String getName(){
			return name;
		}
		
		public int getId(){
			return id.intValue();
		}
	}
	
	public void clearContextMenuItems(){
		lContextMenuItems = null;
	}
}
