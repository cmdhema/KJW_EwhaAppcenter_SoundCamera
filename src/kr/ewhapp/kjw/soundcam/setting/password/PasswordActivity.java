package kr.ewhapp.kjw.soundcam.setting.password;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.gallery.*;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_password)
public class PasswordActivity extends Activity {

	@ViewById(R.id.pw1)
	ImageView pw1;
	@ViewById(R.id.pw2)
	ImageView pw2;
	@ViewById(R.id.pw3)
	ImageView pw3;
	@ViewById(R.id.pw4)
	ImageView pw4;
	@Extra
	String flag;
	
	int clickNum;
	String password="";
	
	@AfterViews
	void init() {
		changePwImage();
	}
	
	private void changePwImage() {
		switch(clickNum) {
		case 1:
			pw1.setImageResource(R.drawable.pw_star);
			break;
		case 2:
			pw2.setImageResource(R.drawable.pw_star);
			break;
		case 3:
			pw3.setImageResource(R.drawable.pw_star);
			break;
		case 4:
			pw4.setImageResource(R.drawable.pw_star);
			checkPassword();
			break;
		}
	}

	private void checkPassword() {
		SharedPreferences shp = getSharedPreferences("sdc", MODE_PRIVATE);
		String pw = shp.getString("password", "-1");
		
		if ( flag.equals(("startFromMain")) ) {
			if ( password.equals(pw) ) {
				Intent intent = new Intent(this, FolderActivity_.class);
				startActivity(intent);
				finish();
			} else {
				pw1.setImageResource(R.drawable.pw);
				pw2.setImageResource(R.drawable.pw);
				pw3.setImageResource(R.drawable.pw);
				pw4.setImageResource(R.drawable.pw);
				clickNum = 0;
				password = "";
				
				Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
			}	
		} else if ( flag.equals("startFromPasswordChange")) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("password", password);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

	@Click
	void password_0() {
		clickNumButton("0");
	}
	
	@Click
	void password_1() {
		clickNumButton("1");

	}
	@Click
	void password_2() {
		clickNumButton("2");

	}
	@Click
	void password_3() {
		clickNumButton("3");

	}
	@Click
	void password_4() {
		clickNumButton("4");

	}
	@Click
	void password_5() {
		clickNumButton("5");

	}
	@Click
	void password_6() {
		clickNumButton("6");

	}
	@Click
	void password_7() {
		clickNumButton("7");

	}
	@Click
	void password_8() {
		clickNumButton("8");

	}
	@Click
	void password_9() {
		clickNumButton("9");

	}

	
	private void clickNumButton(String num) {
		clickNum++;
		password+=num;
		changePwImage();
	}
		

}
