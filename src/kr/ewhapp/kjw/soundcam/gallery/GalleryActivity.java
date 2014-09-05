package kr.ewhapp.kjw.soundcam.gallery;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.image.*;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import kr.ewhapp.kjw.soundcam.image.SdcData.SoundDataListener;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_gallery)
public class GalleryActivity extends Activity implements ImageDataListener, SoundDataListener, OnClickListener, OnItemClickListener {

	HashMap<String, ArrayList<String>> map = SoundCamData.getInstance().getSdcFileListMap();
	ArrayList<String> fileList;
	ArrayList<byte[]> headerImageDataList;
	ArrayList<byte[]> gridImageDataList;
	ArrayList<Long> headerSoundDataList;
	ArrayList<Long> gridSoundDataList;
	ArrayList<ImageView> headerImageViewList;
	ArrayList<ImageView> headerIsRecordViewList;

	@ViewById(R.id.image1_iv)
	ImageView image1;
	@ViewById(R.id.image2_iv)
	ImageView image2;
	@ViewById(R.id.image3_iv)
	ImageView image3;
	@ViewById(R.id.image4_iv)
	ImageView image4;
	@ViewById(R.id.image5_iv)
	ImageView image5;
	@ViewById(R.id.gallery_play1)
	ImageView isRecord1;
	@ViewById(R.id.gallery_play2)
	ImageView isRecord2;
	@ViewById(R.id.gallery_play3)
	ImageView isRecord3;
	@ViewById(R.id.gallery_play4)
	ImageView isRecord4;
	@ViewById(R.id.gallery_play5)
	ImageView isRecord5;
	
	@ViewById(R.id.photo_gridview)
	ScrollGridView gridView;
	@ViewById(R.id.foldername_tv)
	TextView folderNameTv;
	@Extra
	String folderName;
	@Extra
	int index;
	
	int setImageIndex;
	int setSoundIndex;
	
	GalleryAdapter adapter;
	GalleryView view;
	
	byte[] soundData;
	byte[] imageData;
	
	SdcData sdcData;
	
	@AfterViews
	void init() {
		
		headerIsRecordViewList = new ArrayList<ImageView>();
		gridSoundDataList = new ArrayList<Long>();
		gridImageDataList = new ArrayList<byte[]>();
		headerImageViewList = new ArrayList<ImageView>();
		headerImageDataList = new ArrayList<byte[]>();
		headerSoundDataList = new ArrayList<Long>();
		
		folderNameTv.setText(folderName);
		fileList = map.get(folderName);
		
		initViews();

		if ( fileList.size() >= 5) {
			adapter = new GalleryAdapter(getApplicationContext(), gridImageDataList, gridSoundDataList);
			gridView.setAdapter(adapter);
		}
		
	}

	private void initViews() {
		sdcData = new SdcData();
		sdcData.setImageDataLisneter(this);
		sdcData.setSoundDataLisneter(this);
		
		headerImageViewList.clear();
		headerIsRecordViewList.clear();
		headerImageDataList.clear();
		headerSoundDataList.clear();
		
		headerImageViewList.add(image1);
		headerImageViewList.add(image2);
		headerImageViewList.add(image3);
		headerImageViewList.add(image4);
		headerImageViewList.add(image5);
		headerIsRecordViewList.add(isRecord1);
		headerIsRecordViewList.add(isRecord2);
		headerIsRecordViewList.add(isRecord3);
		headerIsRecordViewList.add(isRecord4);
		headerIsRecordViewList.add(isRecord5);
		
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		image3.setOnClickListener(this);
		image4.setOnClickListener(this);
		image5.setOnClickListener(this);
		gridView.setOnItemClickListener(this);

		setImageIndex = 0;
		setSoundIndex = 0;
		Log.i("GalleryActivity, onActivityResult", "fileList Size : " + fileList.size()+"¿‘¥œ¥Ÿ.");
		
		for ( int i = 0; i < fileList.size(); i++ ) {
			sdcData.getSoundData(fileList.get(i));
			sdcData.getImageData(fileList.get(i));
		}
		
		view = new GalleryView(this);
		view.setHeaderView(headerImageViewList, headerIsRecordViewList, headerImageDataList, headerSoundDataList);		
	}

	@Override
	public void imageSize(long size) {
		
	}

	@Override
	public void imageData(byte[] data) {
		
		imageData = data;
		
		if ( setImageIndex < 5) {
			headerImageDataList.add(data);
		} else
			gridImageDataList.add(data);
		
		setImageIndex++;
	}

	@Override
	public void soundSize(long size) {
		if ( setSoundIndex < 5) {
			headerSoundDataList.add(size);
		} else {
			gridSoundDataList.add(size);
		}
		
		setSoundIndex++;
	}

	@Override
	public void soundData(byte[] data) {
		soundData = data;
	}

	@Override
	public void onClick(View v) {
		int index = headerImageViewList.indexOf(v);
		
		Intent intent = new Intent(this, ImagePopupActivity_.class);
		intent.putExtra("folderName", folderName);
		intent.putExtra("filePath", fileList.get(index));
		startActivityForResult(intent, 100);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Intent intent = new Intent(this, ImagePopupActivity_.class);
		intent.putExtra("folderName", folderName);
		intent.putExtra("filePath", fileList.get(position+5));
		startActivityForResult(intent, 100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ( resultCode == 200 ) {

			String filePath = data.getStringExtra("filePath");
			byte[] soundData = data.getByteArrayExtra("soundData");
			byte[] imageData = data.getByteArrayExtra("imageData");
			
			fileList.remove(filePath);
//			Toast.makeText(getApplicationContext(), gridImageDataList.indexOf(imageData)+" ", 0).show();
			if ( adapter != null ) {

				if ( gridImageDataList.remove(imageData) ) 
					Toast.makeText(getApplicationContext(), "Remove", 0).show();
				else
					Toast.makeText(getApplicationContext(), "False", 0).show();
				gridImageDataList.remove(imageData);
				if ( soundData != null )
					gridSoundDataList.remove(soundData);
				
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						adapter.notifyDataSetChanged();
//					}
//				});

//				adapter.notifyDataSetChanged();
				
			} else {
				initViews();
			}
		}
	}
	
}
