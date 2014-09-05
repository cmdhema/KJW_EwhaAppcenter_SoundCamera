package kr.ewhapp.kjw.soundcam.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class SoundCamData {

	private static SoundCamData instance;

	private ArrayList<String> sdcFileList;
	private HashMap<String, ArrayList<String>> sdcFileListMap;
	
	public static String appPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundCamera/";
	public static String galleryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundCamera/gallery/";
	public static String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundCamera/temp/";
	public static String galleryDefaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundCamera/gallery/default/";
	
	private SoundCamData() {
		sdcFileList = new ArrayList<String>();
		sdcFileListMap = new HashMap<String, ArrayList<String>>();
	}

	public static SoundCamData getInstance() {
		if ( instance == null )
			instance = new SoundCamData();
		return instance;
	}

	public ArrayList<String> getSdcFileList() {
		return sdcFileList;
	}

	public HashMap<String, ArrayList<String>> getSdcFileListMap() {
		return sdcFileListMap;
	}
	
}
