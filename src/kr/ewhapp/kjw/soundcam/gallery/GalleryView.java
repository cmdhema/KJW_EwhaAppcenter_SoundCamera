package kr.ewhapp.kjw.soundcam.gallery;

import java.util.ArrayList;

import kr.ewhapp.kjw.soundcam.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidquery.AQuery;

public class GalleryView extends FrameLayout {

	private Context context;
	
	private ImageView galleryItem;
	private ImageView soundBtn;
	private AQuery aq;
	
	public GalleryView(Context context) {
		super(context);
		
		this.context = context;
		
		LayoutInflater.from(context).inflate(R.layout.layout_gallery_row, this);
		aq = new AQuery(context);
		galleryItem = (ImageView) findViewById(R.id.gallery_item);
		soundBtn = (ImageView) findViewById(R.id.gallery_item_playbtn);
		
	}
	
	protected void setGridView(GalleryData galleryData) {
		
		Bitmap bitmap = getBitmap(galleryData.data);
		aq.id(galleryItem).image(bitmap);
		
		if ( galleryData.soundSize != -1 )
			soundBtn.setVisibility(View.VISIBLE);
	}
	
	protected void setHeaderView(ArrayList<ImageView> headerViewList, ArrayList<ImageView> headerIsRecordViewList, ArrayList<byte[]> headerDataList, ArrayList<Long> headerSoundDataList) {
		Log.i("GalleryView", headerDataList.size()+", " + headerSoundDataList.size());
		for ( int i = 0; i < headerDataList.size(); i++ ) {
			
			Bitmap bitmap = getBitmap(headerDataList.get(i));
			headerViewList.get(i).setBackgroundResource(R.drawable.image_border);
			aq.id(headerViewList.get(i)).image(bitmap);
			if ( headerSoundDataList.get(i) != -1 ) 
				headerIsRecordViewList.get(i).setVisibility(View.VISIBLE);
			
		}

		Log.i("GalleryView, setHeaderView", "HeaderDataList Size : " + headerDataList.size()+"ÀÔ´Ï´Ù.");
		for ( int i = headerDataList.size(); i < headerViewList.size(); i++ ) 
			headerViewList.get(i).setVisibility(View.INVISIBLE);
	}
	
	public Bitmap getBitmap(byte[] data) {
		
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, op);
		return bitmap;
	}
}
