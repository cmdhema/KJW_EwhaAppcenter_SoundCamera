package kr.ewhapp.kjw.soundcam.image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import kr.ewhapp.kjw.soundcam.image.SdcData.SoundDataListener;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import kr.ewhapp.kjw.soundcam.record.RecordTask;
import kr.ewhapp.kjw.soundcam.util.FileUtils;
import kr.ewhapp.kjw.soundcam.util.SdcConverter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_imagepopup)
public class ImagePopupActivity extends Activity implements SoundDataListener, ImageDataListener {

	HashMap<String, ArrayList<String>> map;
	
	@ViewById(R.id.photo_pager)
	GalleryViewPager viewPager;
	
	FilePagerAdapter fileAdapter;
	
	long fileName;
	@Extra("filePath")
	String filePath;
	@Extra("folderName")
	String folderName;
	@ViewById(R.id.seperate_btn)
	Button seperateBtn;
	@ViewById(R.id.convert_btn)
	Button convertBtn;
	@ViewById(R.id.move_btn)
	Button moveBtn;
	@ViewById(R.id.remove_btn)
	Button removeBtn;
	@ViewById(R.id.play_btn)
	ImageView playBtn;
	
	String fileFolderPath;
	
	byte[] imageData;
	byte[] soundData;
	
	private ArrayList<String> soundImageFilePathList;
	
	private SdcConverter converter;
	
	@AfterInject
	void initList() {
		SoundCamData.getInstance().getSdcFileListMap().clear();
		FileUtils.getFileList(SoundCamData.appPath);
	
		map = SoundCamData.getInstance().getSdcFileListMap();
		
	}
	
	@AfterViews
	void init() {
		
		fileFolderPath = new File(filePath).getParentFile().getAbsolutePath() + "/";
		fileName = FileUtils.getFileName(filePath);
		converter = new SdcConverter();
		final SdcData data = new SdcData();
		data.setImageDataLisneter(this);
		data.setSoundDataLisneter(this);
		data.getImageData(filePath);
		data.getSoundData(filePath);
		soundImageFilePathList = map.get(folderName);
		fileAdapter = new FilePagerAdapter(this, soundImageFilePathList);
		fileAdapter.setOnItemChangeListener(new OnItemChangeListener() {
			
			@Override
			public void onItemChange(int currentPosition) {
				filePath = soundImageFilePathList.get(currentPosition);
				data.getSoundData(filePath);
				data.getImageData(filePath);
			}
		});
		viewPager.setAdapter(fileAdapter);
		viewPager.setCurrentItem(soundImageFilePathList.indexOf(filePath));
	}

	@Click(R.id.play_btn)
	void soundPlay() {
		RecordTask.getInstance().startPlay(soundData);
	}
	
	@Click(R.id.seperate_btn)
	void seperate() {
		String imageFilePath = fileFolderPath + fileName + ".jpg";
		String soundFilePath = fileFolderPath + fileName + ".wav";
		
		converter.convertImageDataToJpg(imageFilePath, imageData);
		
		if ( soundData != null)
			converter.convertSoundDataToWave(soundFilePath, soundData);
	}
	
	@Click(R.id.convert_btn)
	void convertToVideo() {
		String outputPath = fileFolderPath + fileName + ".mp4";
		converter.convertFileToVideo(imageData, soundData, outputPath);
	}
	
	@Click(R.id.remove_btn)
	void removeFile() {
		new AlertDialog.Builder(this).setTitle("파일 삭제").setMessage("사진을 삭제하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FileUtils.removeFile(filePath);
				Intent intent =  new Intent();
				intent.putExtra("filePath", filePath);
				intent.putExtra("imageData", imageData);
				Log.i("ImagePopupActivity", "넘기기전 : " + imageData.length);
				if ( soundData != null )
					intent.putExtra("soundData", soundData);
				setResult(200, intent);
				ImagePopupActivity.this.finish();
			}
		}).setNegativeButton("아니오", null).show();

	}
	
	@Click(R.id.move_btn)
	void moveFile() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(filePath);
		FolderSelectDialog dialog = new FolderSelectDialog(this);
		dialog.setDialog(FileUtils.getFolderNameList(map), list);
		dialog.show();
	}

	@Override
	public void imageSize(long size) {
		
	}

	@Override
	public void imageData(byte[] data) {
		this.imageData = data;
	}

	@Override
	public void soundSize(long size) {
		if ( size == -1 ) {
			convertBtn.setVisibility(View.INVISIBLE);
			playBtn.setVisibility(View.INVISIBLE);
		} else {
			seperateBtn.setVisibility(View.VISIBLE);
			convertBtn.setVisibility(View.VISIBLE);
			playBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void soundData(byte[] data) {
		this.soundData = data;
	}
}
