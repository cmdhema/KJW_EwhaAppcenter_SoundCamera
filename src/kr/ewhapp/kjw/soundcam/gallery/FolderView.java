package kr.ewhapp.kjw.soundcam.gallery;

import kr.ewhapp.kjw.soundcam.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

public class FolderView extends FrameLayout implements OnClickListener {

	private TextView nameTv;
	private LinearLayout galleryLayout;
	private Context context;
	private AQuery aq;
	
	private OnPhotoItemClickListener listener;
	
	
	public FolderView(Context context) {
		super(context);
		this.context = context;
		
		LayoutInflater.from(context).inflate(R.layout.layout_folder_row, this);
		nameTv = (TextView) findViewById(R.id.albumfolder_tv);
		galleryLayout = (LinearLayout) findViewById(R.id.gallery_linearlayout);
		aq = new AQuery(context);
	}
	
	public void setView(FolderData data) {
		
		Log.i("FolderView", data.folderName);
		
		nameTv.setText(data.folderName);

		galleryLayout.removeAllViews();
		
		for ( int i = 0; i < data.imageDataList.size(); i++ ) {
			ImageView photoView = new ImageView(context);
			photoView.setLayoutParams(new LayoutParams(200, 150));
			photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			photoView.setPadding(10, 5, 10, 5);
			photoView.setTag((String)data.folderName);
			photoView.setBackgroundResource(R.drawable.image_border);
//			Bitmap bitmap = BitmapFactory.decodeByteArray(data.imageDataList.get(i), 0, (int) data.imageDataList.get(i).length );
			Bitmap bitmap = getBitmap(data.imageDataList.get(i));
			aq.id(photoView).image(bitmap);
			photoView.setOnClickListener(this);
			galleryLayout.addView(photoView);
		}
		
	}

	@Override
	public void onClick(View v) {
		listener.onPhotoClick(galleryLayout.indexOfChild(v), (String)v.getTag());
	}
	
	public void setOnPhotoItemClickListener(OnPhotoItemClickListener listener) {
		this.listener = listener;
	}
	
	public interface OnPhotoItemClickListener {
		public void onPhotoClick(int position, String folderName);
	}
	
	public Bitmap getBitmap(byte[] data) {

		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, op);
		
		return bitmap;
	}
	
}
