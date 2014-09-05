package kr.ewhapp.kjw.soundcam.splash;

import java.io.File;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import kr.ewhapp.kjw.soundcam.main.*;
import kr.ewhapp.kjw.soundcam.util.FileUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.ImageView;

@EActivity(R.layout.layout_splash)
public class SplashActivity extends Activity {

	private static final int DELAY_TIME = 1500;
	private int splashCount;

	@ViewById(R.id.splash_background_iv)
	ImageView backgroundIv;

	@AfterInject
	void init() {

		makeDefaultDir();
		getFileList();
		setInitValue();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (splashCount == 0) {
					splashCount++;
					// backgroundIv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
					// android.R.anim.fade_out));
					// backgroundIv.setVisibility(View.INVISIBLE);
					new Handler().postDelayed(this, DELAY_TIME);
					return;
				}

				startActivity(new Intent(SplashActivity.this, SoundCameraMainActivity_.class));

				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();

			}
		}, DELAY_TIME);
	}

	private void setInitValue() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		if ( getPassword().equals("-1")) {
			editor.putString("password", "-1");
			editor.commit();
		}

	}

	private void getFileList() {
		SoundCamData.getInstance().getSdcFileListMap().clear();
		FileUtils.getFileList(SoundCamData.appPath);
	}

	private void makeDefaultDir() {
		File file = new File(SoundCamData.appPath);
		File defaultPhotoFolder = new File(SoundCamData.galleryDefaultPath);
		File galleryFolder = new File(SoundCamData.galleryPath);

		if (!file.exists())
			file.mkdir();
		if (!defaultPhotoFolder.exists())
			defaultPhotoFolder.mkdir();
		if (!galleryFolder.exists())
			galleryFolder.mkdir();
	}

	private String getPassword() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		String pw = pref.getString("password", "-1");
		return pw;
	}

}
