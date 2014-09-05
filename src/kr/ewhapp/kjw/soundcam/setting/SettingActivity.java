
package kr.ewhapp.kjw.soundcam.setting;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.setting.password.*;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_setting)
public class SettingActivity extends Activity {

	@ViewById(R.id.password_cb)
	CheckBox passwordCb;
	
	@ViewById(R.id.capturetime_radio)
	RadioGroup captureTimeRadioGroup;
	
	@AfterViews
	void init() {
		setPasswordCheckState();
		setCaptureTime();
	}
	
	private void setPasswordCheckState() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		int state = pref.getInt("locked", 0);
		if ( state == 0 ) 
			passwordCb.setChecked(false);
		else
			passwordCb.setChecked(true);
	}

	private void setCaptureTime() {
		
		captureTimeRadioGroup.check(getId());
		captureTimeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch(group.getCheckedRadioButtonId()) {
				case R.id.capturetime0_rb :
					saveCaptureTime(0);
					break;
				case R.id.capturetime1_rb :
					saveCaptureTime(1000);
					break;
				case R.id.capturetime3_rb :
					saveCaptureTime(3000);
					break;
				case R.id.capturetime5_rb :
					saveCaptureTime(5000);
					break;
				}
			}
		});
	}

	private int getId() {
		SharedPreferences pef = getSharedPreferences("sdc", MODE_PRIVATE);
		int captureTime = pef.getInt("captureTime", 0);
		
		if ( captureTime == 0 )
			return R.id.capturetime0_rb;
		else if ( captureTime == 1)
			return R.id.capturetime1_rb;
		else if ( captureTime == 3)
			return R.id.capturetime3_rb;
		else
			return R.id.capturetime5_rb;
		
	}

	@CheckedChange(R.id.password_cb)
	void passwordLockChange(CompoundButton checkBox) {
		if ( checkBox.isChecked() ) {
			savePasswordState(1);
		} else {
			savePasswordState(0);
		}
	}
	
	@Click(R.id.passwordchange_btn)
	void changePasswordSetting() {
		Intent intent = new Intent(this, PasswordChangeActivity_.class);
		startActivity(intent);
	}
	
	private void saveCaptureTime( int time ){
		SharedPreferences pef = getSharedPreferences("sdc", MODE_PRIVATE);
		SharedPreferences.Editor editor = pef.edit();
		editor.putInt("captureTime", time);
		editor.commit();
	}
	
	private void savePasswordState(int save) {
		SharedPreferences pef = getSharedPreferences("sdc", MODE_PRIVATE);
		SharedPreferences.Editor editor = pef.edit();
//		editor.putBoolean("lock", save);
		editor.putInt("locked", save);
		editor.commit();
	}
}
