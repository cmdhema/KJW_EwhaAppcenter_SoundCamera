package kr.ewhapp.kjw.soundcam.gallery;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GalleryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<GalleryData> galleryDataList;
	
	public GalleryAdapter(Context c, ArrayList<byte[]> imageList, ArrayList<Long> soundList) {
		mContext = c;
		setGalleryDataList(imageList, soundList);
	}

	private void setGalleryDataList(ArrayList<byte[]> imageList, ArrayList<Long> soundList) {
		
		galleryDataList = new ArrayList<GalleryData>();
		
		for ( int i = 0; i < imageList.size(); i++ ) {
			GalleryData data = new GalleryData();
			data.data = imageList.get(i);
			data.soundSize = soundList.get(i);
			galleryDataList.add(data);
		}
	}

	@Override
	public int getCount() {
		return galleryDataList.size();
	}

	@Override
	public GalleryData getItem(int position) {
		return galleryDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		GalleryView view;
		
		if (convertView == null) {
			view = new GalleryView(mContext);
		} else {
			view = (GalleryView) convertView;
		}

		view.setGridView(galleryDataList.get(position));
		
		return view;
	}


}
