package kr.ewhapp.kjw.soundcam.camera;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.image.*;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import kr.ewhapp.kjw.soundcam.record.RecordTask;
import kr.ewhapp.kjw.soundcam.record.RecordTask.OnRecordListener;
import kr.ewhapp.kjw.soundcam.util.FileUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@WindowFeature(Window.FEATURE_NO_TITLE)
@Fullscreen
@EActivity(R.layout.layout_camera)
public class CameraActivity extends Activity {

	private enum FlashMode {
		FLASH_ON,
		FLASH_OFF,
		FLASH_AUTO
	}
	
	private FlashMode flashMode = FlashMode.FLASH_OFF;
	
	private final int BACK_CAMERA = Camera.CameraInfo.CAMERA_FACING_BACK;
	private final int FRONT_CAMERA = Camera.CameraInfo.CAMERA_FACING_FRONT;

	private int cameraMode = BACK_CAMERA;

	private String soundImageFilePath;

	@ViewById(R.id.btn_capture)
	ImageView captureBtn;
	@ViewById(R.id.btn_flash)
	ImageView flashBtn;
	@ViewById(R.id.btn_preview)
	ImageView preViewImageView;
	@ViewById(R.id.btn_setting)
	ImageView settingBtn;
	@ViewById(R.id.btn_record)
	ImageView recordBtn;
	@ViewById(R.id.btn_mode)
	ImageView cameraModeBtn;
	@ViewById(R.id.previewFrame)
	FrameLayout previewFrame;
	@ViewById(R.id.preview)
	SurfaceView surfaceView;
	@ViewById(R.id.delaytime_tv)
	TextView delayTimeTv;

	private CameraPreview cameraView;
	private Camera camera;
	protected long imageFileName;
	private long soundFileSize;
	private byte[] soundData;
	private byte[] imageData;

	private OrientationEventListener oriListener;
	
	private Animation animTopToRight;
	private Animation animTopToLeft;
	private Animation animRightToTop;
	private Animation animRightToBottom;
	private Animation animLeftToTop;
	private Animation animLeftToBottom;
	private Animation animBottomToRight;
	private Animation animBottomToLeft;
	
	private ArrayList<Integer> angleList;
	
	private Camera.Parameters camParams;

	private int bottomCount;
	private int topCount;
	private int leftCount;
	private int rightCount;
	
	protected boolean changeFlag;

	private int maxZoom;
	private int currentZoom;

	private AnimationDrawable recBtnAnimation;

	private int recordDelayTime;
	int delaymSec = 99;
	int delaySec;

	@AfterViews
	void init() {
		setRecordDelayTime();
		setPreviewImage();
//		setCamera();
		initAnimation();
	}

	@Override
	protected void onResume() {
		super.onResume();

		setCamera();
		
//		cameraView = new CameraPreview(this, surfaceView);
//		cameraView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		cameraView.setKeepScreenOn(true);
//		previewFrame.removeView(cameraView);
//		previewFrame.addView(cameraView);
//		
//		cameraView.setCamera(cameraMode);

		delaySec = getRecordDelayTime() / 1000;

		oriListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_FASTEST) {

			@Override
			public void onOrientationChanged(int angle) {
				
				angleList.add(angle);

				if ((angle >= 350 && angle <= 360) || (angle >= 0 && angle <= 10)) {
					bottomCount = 0;
					leftCount = 0;
					rightCount = 0;
					
					if (topCount == 0) {
						topCount++;

						if ( angle >= 350 && angle <= 360 ) 
							setAnimation(animTopToLeft);
						else if ( angle >= 0 && angle <= 10 ) 
							setAnimation(animBottomToLeft);
						
					}
				} else if ( ( angle >= 80 && angle <= 89 ) || ( angle >= 91 && angle <= 100 ) ) {
					
					bottomCount = 0;
					leftCount = 0;
					topCount = 0;
					
					if (rightCount == 0) {
						rightCount++;
						
						if ( angle >= 80 && angle <= 89) 
							setAnimation(animLeftToBottom);	
						else if ( angle >= 91 && angle <= 100 )
							setAnimation(animRightToBottom);
						
					}
				} else if ( ( angle >= 170 && angle<=179 ) || ( angle >= 181 && angle <= 190 ) ) {
					
					rightCount = 0;
					leftCount = 0;
					topCount = 0;
					
					if (bottomCount == 0) {
						bottomCount++;
						
						if ( angle >= 170 && angle<=179 )
							setAnimation(animBottomToRight);
						else if ( angle >= 181 && angle <= 190 ) 
							setAnimation(animTopToRight);
					
					}
				} else if ( ( angle >= 260 && angle <= 269 ) || ( angle >= 271 && angle <= 280 ) ) {
					
					rightCount = 0;
					bottomCount = 0;
					topCount = 0;
					
					if (leftCount == 0) {
						leftCount++;
						
						if ( angle >= 260 && angle <= 269 )
							setAnimation(animRightToTop);
						else if ( angle >= 271 && angle <= 280 ) 	
							setAnimation(animLeftToTop);
					}
				}
			}
		};
		
		oriListener.enable();
	}
	
	private int getRecordDelayTime() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		return pref.getInt("captureTime", 0);
	}

	private void initAnimation() {
		
//		animTopToRight = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animTopToLeft = new RotateAnimation(360, 270, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animRightToTop = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animRightToBottom = new RotateAnimation(90, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animLeftToTop = new RotateAnimation(270, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animLeftToBottom = new RotateAnimation(270, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animBottomToRight = new RotateAnimation(180, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animBottomToLeft = new RotateAnimation(180, 270, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		animTopToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_top_to_right);
		animTopToLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_top_to_left);
		animRightToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_right_to_top);
		animRightToBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_right_to_bottom);
		animLeftToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_left_to_top);
		animLeftToBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_left_to_bottom);
		animBottomToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_bottom_to_right);
		animBottomToLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_bottom_to_left);
		
		animTopToLeft.setStartOffset(0);
		animTopToRight.setStartOffset(0);
		animLeftToTop.setStartOffset(0);
		animLeftToBottom.setStartOffset(0);
		animRightToTop.setStartOffset(0);
		animRightToBottom.setStartOffset(0);
		animBottomToLeft.setStartOffset(0);
		animBottomToRight.setStartOffset(0);
		
		animTopToLeft.setFillAfter(true);
		animTopToRight.setFillAfter(true);
		animLeftToTop.setFillAfter(true);
		animLeftToBottom.setFillAfter(true);
		animRightToTop.setFillAfter(true);
		animRightToBottom.setFillAfter(true);
		animBottomToLeft.setFillAfter(true);
		animBottomToRight.setFillAfter(true);
		
		animTopToLeft.setDuration(1000);
		animTopToRight.setDuration(1000);
		animLeftToTop.setDuration(1000);
		animLeftToBottom.setDuration(1000);
		animRightToTop.setDuration(1000);
		animRightToBottom.setDuration(1000);
		animBottomToLeft.setDuration(1000);
		animBottomToRight.setDuration(1000);

		recordBtn.setBackgroundResource(R.anim.recordbtn_animation);
		recBtnAnimation = (AnimationDrawable) recordBtn.getBackground();
	}
	
	private void setAnimation(Animation anim) {

		angleList.clear();		
		settingBtn.startAnimation(anim);
		cameraModeBtn.startAnimation(anim);
		flashBtn.startAnimation(anim);
		preViewImageView.startAnimation(anim);
		captureBtn.startAnimation(anim);
		recordBtn.startAnimation(anim);
	}

	private void setRecordDelayTime() {
		SharedPreferences pref = getSharedPreferences("sdc", MODE_PRIVATE);
		int time = pref.getInt("captureTime", 0);
		recordDelayTime = time * 1000;
	}
	
	public void setCamera() {

		angleList = new ArrayList<Integer>();
		
		cameraView = new CameraPreview(this, surfaceView);
		cameraView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		cameraView.setKeepScreenOn(true);
		previewFrame.removeView(cameraView);
		previewFrame.addView(cameraView);
		
		cameraView.setCamera(cameraMode);
		camera = cameraView.getCamera();
		camParams = camera.getParameters();

		if (camParams.isZoomSupported())
			maxZoom = camParams.getMaxZoom();
	}

	@Click(R.id.btn_record)
	void record() {
		if (RecordTask.getInstance().getStatus() == RecordTask.PLAYING) {
			showPauseRecordDialog();
		} else if (RecordTask.getInstance().getStatus() == RecordTask.STOP || RecordTask.getInstance().getStatus() == RecordTask.PAUSE || RecordTask.getInstance().getStatus() == RecordTask.INIT) {
			showStartRecordDialog();
		}
	}

	@Click(R.id.btn_preview)
	void showPreview() {
		Intent intent = new Intent(this, ImagePopupActivity_.class);
		
		if ( soundImageFilePath == null )
			soundImageFilePath = FileUtils.lastFilePath;
		
		intent.putExtra("filePath", soundImageFilePath);
		Log.i("CameraActivity", "Path : " + soundImageFilePath);
		intent.putExtra("folderName", "default");
		startActivity(intent);
	}

	@Click(R.id.btn_mode)
	void changeCameraMode() {

		previewFrame.removeAllViews();

		if (cameraMode == FRONT_CAMERA) {
			cameraMode = BACK_CAMERA;
			recordBtn.setClickable(true);
		} else {
			cameraMode = FRONT_CAMERA;
			recordBtn.setClickable(false);
		}
		cameraView.setCamera(cameraMode);
		previewFrame.removeView(cameraView);
		previewFrame.addView(cameraView);
//		setCamera();
	}	

	@SuppressWarnings("static-access")
	@Click(R.id.btn_flash)
	void setFlash() {

		if ( flashMode == FlashMode.FLASH_AUTO) {
			flashBtn.setImageDrawable(getResources().getDrawable(R.drawable.flash_off_btn));
			camParams.setFlashMode(Parameters.FLASH_MODE_OFF);
			flashMode = FlashMode.FLASH_OFF;
		} else if ( flashMode == flashMode.FLASH_ON) {
			flashBtn.setImageDrawable(getResources().getDrawable(R.drawable.flash_auto_btn));
			camParams.setFlashMode(Parameters.FLASH_MODE_AUTO);
			flashMode = FlashMode.FLASH_AUTO;
		} else if ( flashMode == flashMode.FLASH_OFF) {
			flashBtn.setImageDrawable(getResources().getDrawable(R.drawable.flash_on_btn));
			camParams.setFlashMode(Parameters.FLASH_MODE_ON);
			flashMode = FlashMode.FLASH_ON;
		}

		camera.setParameters(camParams);
	}

	@Click(R.id.previewFrame)
	void setFocus() {

		camera.autoFocus(new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					// TODO
					//Focus success Message or Image
				} else {
					
				}
			}
		});
	}

	private void showStartRecordDialog() {
		new AlertDialog.Builder(this).setTitle("녹음").setMessage("녹음을 시작하시겠습니까?").setNegativeButton("취소", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

		}).setPositiveButton("예", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startRecordThread();
				recBtnAnimation.start();
			}
		}).show();
	}

	@Click(R.id.btn_capture)
	void takePicture() {

		/*
		 *  **파일 구조**
		 * 생성 일자
		 * 사진 데이터 길이
		 * 사진 데이터
		 * 음성 데이터 길이
		 * 음성 데이터
		 */
		
		cameraView.capture(new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					imageData = new byte[data.length];
					imageData = data;
					imageFileName = System.currentTimeMillis();
					soundImageFilePath = SoundCamData.galleryDefaultPath + imageFileName + ".sdc";

					final FileOutputStream fos = new FileOutputStream(soundImageFilePath);
					final DataOutputStream dos = new DataOutputStream(fos);
//					dos.writeUTF(soundImageFilePath);
					dos.writeLong(System.currentTimeMillis());
					dos.writeLong(data.length);
					dos.write(data);
					if ( RecordTask.getInstance().getStatus() != RecordTask.INIT) {
						
						RecordTask.getInstance().setOnRecordListener(new OnRecordListener() {

							@Override
							public void onSuccess(ArrayList<LinkedList<Byte>> list, long size) {
								Log.d(" CameraActivity", " Audio Size : " + size);

								soundFileSize = size;
								soundData = new byte[(int) soundFileSize];

								try {
									dos.writeLong(soundFileSize);

									int j = 0;

									for (int i = 0; i < list.size(); i++) {
										Iterator<Byte> iterator = list.get(i).iterator();

										while (iterator.hasNext()) {
											soundData[j] = iterator.next();
											j++;
										}
									}

									dos.write(soundData);
									fos.close();
									dos.close();

								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}

							@Override
							public void onError() {
								Log.i("CameraActivity", "OnError");
							}
						});

						stopRecordThread();
					} else {
						Log.i("CameraActivity", "None Record");
						dos.writeLong((long)-1);
						dos.write(0);
						fos.close();
						dos.close();
					}

					setPreviewImage(data);
					camera.startPreview();

				} catch (Exception e) {
				}
			}
		});
	}

	private void showPauseRecordDialog() {
		new AlertDialog.Builder(this).setTitle("녹음").setMessage("녹음을 일시정지하시겠습니까?").setNegativeButton("취소", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

		}).setPositiveButton("예", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				pauseRecordThread();
				recBtnAnimation.stop();
			}
		}).show();
	}

	// private void showStopRecordDiaglog() {
	// new
	// AlertDialog.Builder(this).setTitle("녹음").setMessage("녹음을 중지하시겠습니까?").setNegativeButton("취소",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {}
	//
	// }).setPositiveButton("예", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// stopRecordThread();
	// }
	// }).show();
	// }

	protected void pauseRecordThread() {
		RecordTask.getInstance().pauseRecord();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			if (currentZoom > 0) {
				currentZoom--;
				camParams.setZoom(currentZoom);
				camera.setParameters(camParams);
			}
		} else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			if (currentZoom < maxZoom) {
				currentZoom++;
				camParams.setZoom(currentZoom);
				camera.setParameters(camParams);
			}
		} else if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.onBackPressed();
		}

		return true;

	}

	protected void stopRecordThread() {
		
		@SuppressLint("HandlerLeak")
		class TimerHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				String smsec = String.valueOf(delaymSec);
				if (msg.what == 0) {
					if (delaySec == -1 && smsec.equals("99")) {
						delaySec = 0;
						smsec = "00";
					}

					delayTimeTv.setText("0" + delaySec + " : " + smsec);
				}
			}
		}

		final TimerHandler handler = new TimerHandler();

		final TimerTask task = new TimerTask() {

			@Override
			public void run() {

				if (delaySec == recordDelayTime / 1000 && delaymSec == 99)
					delaySec--;
				delaymSec--;

				if (delaymSec == 0) {
					delaymSec = 99;
					delaySec--;

					if (delaySec == -1) {
						this.cancel();
						RecordTask.getInstance().stopRecord();
						recBtnAnimation.stop();
					}

				}
				handler.sendMessage(Message.obtain(handler, 0));
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 0, 10);
		
		RecordTask.getInstance().setStatus(RecordTask.INIT);
	}

	protected void startRecordThread() {
		RecordTask.getInstance().startRecord();
	}

	private void setPreviewImage() {
		
		
		SdcData data = new SdcData();
		data.setImageDataLisneter(new ImageDataListener() {
			
			@Override
			public void imageSize(long size) {
				
			}
			
			@Override
			public void imageData(byte[] data) {
				setPreviewImage(data);
			}
		});
		
		if ( FileUtils.lastFilePath != null )
			data.getImageData(FileUtils.lastFilePath);
	}

	protected void setPreviewImage(byte[] data) {

		Bitmap preViewImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		preViewImageView.setImageBitmap(preViewImage);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		stopRecordThread();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopRecordThread();
	}
}
