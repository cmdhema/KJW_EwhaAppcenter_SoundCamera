package kr.ewhapp.kjw.soundcam.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.camera.*;
import kr.ewhapp.kjw.soundcam.gallery.*;
import kr.ewhapp.kjw.soundcam.setting.*;
import kr.ewhapp.kjw.soundcam.setting.password.*;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Window;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_main)
public class SoundCameraMainActivity extends Activity {

	HashMap<String, ArrayList<String>> fileMap = SoundCamData.getInstance().getSdcFileListMap();

	@AfterInject
	void initWindow() {
		Set<String> keyList = fileMap.keySet();
		Iterator<String> it = keyList.iterator();

		while (it.hasNext()) {
			String folderName = it.next();
			ArrayList<String> path = fileMap.get(folderName);
			System.out.println("Folder : " + folderName);
			for (int i = 0; i < path.size(); i++) {
				System.out.println("File : " + path.get(i));
			}
		}
	}

	@AfterViews
	void init() {

	}

	@Click(R.id.btn_camera)
	void startCamera() {
		startActivity(new Intent(this, CameraActivity_.class));
	}

	@Click(R.id.btn_gallery)
	void startGallery() {
		if (isLocked() == 1) { 
			Intent intent = new Intent(this, PasswordActivity_.class);
			intent.putExtra("flag", "startFromMain");
			startActivity(intent);
		}
		else 
			startActivity(new Intent(this, FolderActivity_.class));
	}

	@Click(R.id.btn_setting)
	void startSetting() {
		startActivity(new Intent(this, SettingActivity_.class));
	}

	private int isLocked() {
		SharedPreferences sp = getSharedPreferences("sdc", MODE_PRIVATE);
		return sp.getInt("locked", 0);
	}

}
