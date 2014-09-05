package kr.ewhapp.kjw.soundcam.gallery;

import java.util.ArrayList;

import kr.ewhapp.kjw.soundcam.gallery.FolderView.OnPhotoItemClickListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FolderListAdapter extends BaseAdapter implements OnPhotoItemClickListener {

	private ArrayList<FolderData> dataList;
	private Context context;
	
	private OnPhotoItemClickListener listener;
	
	public FolderListAdapter(Context context, ArrayList<FolderData> list) {
		this.context = context;
		this.dataList = list;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public FolderData getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		
		FolderView view;
		
		if ( convertView == null)
			view = new FolderView(context);
		else
			view = (FolderView) convertView;

		view.setView(dataList.get(position));
		view.setOnPhotoItemClickListener(this);
		
		return view;
	}
	
	public void setOnPhotoItemClickListener(OnPhotoItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onPhotoClick( int position, String folderName ) {
		listener.onPhotoClick(position, folderName);
	}
	
}
