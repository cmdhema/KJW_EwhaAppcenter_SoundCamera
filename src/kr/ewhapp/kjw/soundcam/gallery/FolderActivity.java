package kr.ewhapp.kjw.soundcam.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.ewhapp.kjw.soundcam.R;
import kr.ewhapp.kjw.soundcam.gallery.FolderView.OnPhotoItemClickListener;
import kr.ewhapp.kjw.soundcam.image.SdcData;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import kr.ewhapp.kjw.soundcam.util.FileUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity(R.layout.layout_folder)
public class FolderActivity extends Activity implements ImageDataListener, OnPhotoItemClickListener {

	HashMap<String, ArrayList<String>> fileMap = SoundCamData.getInstance().getSdcFileListMap();
	ArrayList<byte[]> imageDataList;
	ArrayList<FolderData> folderDataList;
	ArrayList<String> folderNameList;
	SdcData soundCameraData;
	
	@ViewById(R.id.gallery_newfolder_tv)
	TextView newFolderTv;
	@ViewById(R.id.folder_listview)
	ListView folderListView;

	@Override
	protected void onResume() {
		super.onResume();

		SoundCamData.getInstance().getSdcFileListMap().clear();
		FileUtils.getFileList(SoundCamData.appPath);
		
		folderDataList = new ArrayList<FolderData>();
		soundCameraData = new SdcData();
		soundCameraData.setImageDataLisneter(this);
		
		Set<String> keyList = fileMap.keySet();
		Iterator<String> it = keyList.iterator();
		
		FolderData folderData;
		while ( it.hasNext() ) {
			
			imageDataList = new ArrayList<byte[]>();
			String folderName = it.next();
			ArrayList<String> path = fileMap.get(folderName);

			for ( int i = 0; i < path.size(); i++ ) 
				soundCameraData.getImageData(path.get(i));
			
			folderData = new FolderData();
			folderData.folderName = folderName;
			folderData.imageDataList = imageDataList;
			
			if ( imageDataList.size() != 0 ) {
				if ( folderData.folderName.equals("default"))
					folderDataList.add(0, folderData);
				else
					folderDataList.add(folderData);
			}

		}

		FolderListAdapter folderListAdapter = new FolderListAdapter(getApplicationContext(), folderDataList);
		folderListAdapter.setOnPhotoItemClickListener(this);
		folderListView.setAdapter(folderListAdapter);
	
	}

	
	@Click(R.id.gallery_newfolder_tv)
	void makeDir() {

		showMakeDirDialog();

	}

	private void showMakeDirDialog() {
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView nameTv = new TextView(this);
		nameTv.setText("폴더 명을 입력하세요");
		nameTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		final EditText nameEt = new EditText(this);
		nameEt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		ll.addView(nameTv);
		ll.addView(nameEt);

		new AlertDialog.Builder(this).setView(ll).setTitle("새 폴더 만들기").setNegativeButton("취소", null).setPositiveButton("만들기", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String dirName = nameEt.getText().toString();
				String dirPath = SoundCamData.galleryPath + dirName;
				File dir = new File(dirPath);
				if (dir.mkdir()) {
					Toast.makeText(FolderActivity.this, "폴더 생성 성공", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FolderActivity.this, "폴더 생성 실패", Toast.LENGTH_SHORT).show();
				}
			}
		}).show();
	}

	@Override
	public void imageSize(long size) {
		
	}

	@Override
	public void imageData(byte[] data) {
		imageDataList.add(data);
	}

	@Override
	public void onPhotoClick(int position, String folderName) {
		Intent intent = new Intent(FolderActivity.this, GalleryActivity_.class);
		intent.putExtra("index", position);
		intent.putExtra("folderName", folderName);
		startActivity(intent);
	}
}
