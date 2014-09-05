package kr.ewhapp.kjw.soundcam.image;

import java.util.ArrayList;

import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import kr.ewhapp.kjw.soundcam.util.FileUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

public class FolderSelectDialog extends AlertDialog.Builder {

	private Context context;
	private String folderName;
	
	protected FolderSelectDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void setDialog(final ArrayList<String> folderNameList, final ArrayList<String> fileList) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);

		for ( int i = 0; i < folderNameList.size(); i++ ) 
			arrayAdapter.add(folderNameList.get(i));
		
		this.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		this.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String strName = arrayAdapter.getItem(which);
				folderName = strName;
				AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
				builderInner.setMessage(strName);
				builderInner.setTitle("������ ������ ������ �̵��մϴ�.");
				builderInner.setPositiveButton("��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for ( int i = 0; i < fileList.size(); i++)
							FileUtils.moveFiles(fileList.get(i), SoundCamData.galleryPath + folderName + "/");
						dialog.dismiss();
					}
				}).setNegativeButton("�ƴϿ�", null);
				builderInner.show();
			}
		});
	}
}
