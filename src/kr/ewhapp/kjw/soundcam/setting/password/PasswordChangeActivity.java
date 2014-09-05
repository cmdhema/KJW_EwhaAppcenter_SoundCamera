package kr.ewhapp.kjw.soundcam.setting.password;

import kr.ewhapp.kjw.soundcam.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_passwordchange)
public class PasswordChangeActivity extends Activity implements OnTouchListener {

	@ViewById(R.id.currentpw_layout)
	LinearLayout currentPwLayout;
	@ViewById(R.id.passwordtitle_tv)
	TextView titleTv;
	@ViewById(R.id.passwordnew_et)
	EditText newPwEt;
	@ViewById(R.id.passwordcurrent_et)
	EditText currentPwEt;
	@ViewById(R.id.passwordagain_et)
	EditText againPwEt;

	@Extra
	String flag;
	
	EditText nowEditText;

	int touchCount = 0;

	@AfterViews
	void init() {
		
		if ( getPassword().equals("-1") )
			currentPwLayout.setVisibility(View.GONE);
		
		newPwEt.setCursorVisible(false);
		againPwEt.setCursorVisible(false);
		currentPwEt.setCursorVisible(false);
		newPwEt.setOnTouchListener(this);
		currentPwEt.setOnTouchListener(this);
		againPwEt.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {

		if (touchCount == 0) {
			nowEditText = ((EditText) v);
			Intent intent = new Intent(this, PasswordActivity_.class);
			intent.putExtra("flag", "startFromPasswordChange");
			startActivityForResult(intent, 100);
		}

		touchCount++;

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			nowEditText.setText(data.getStringExtra("password"));
			touchCount = 0;
		}
	}

	@Click
	void passwordchange_okbtn() {
		String newPw = newPwEt.getText().toString();
		String againPw = againPwEt.getText().toString();
		String currentPw = "";
		
		if ( !getPassword().equals("-1") ) {
			currentPw = currentPwEt.getText().toString();
			if ( newPw.equals(againPw) && currentPw.equals(getPassword()) )
				savePassword(newPw);
			else
				Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
		} else {
			if ( newPw.equals(againPw) )
				savePassword(newPw);
			else
				Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();			
		}

	}
	
	private void savePassword(String newPw) {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("password", newPw);
		editor.commit();
		
		Toast.makeText(getApplicationContext(), "비밀번호 저장 성공.", Toast.LENGTH_SHORT).show();
		finish();
	}

	private String getPassword() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		String pw = pref.getString("password", "-1");
		return pw;
	}

}
