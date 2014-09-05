/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.truba.touchgallery.TouchView;

import java.io.DataInputStream;

import kr.ewhapp.kjw.soundcam.image.SdcData;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

public class FileTouchImageView extends UrlTouchImageView 
{
	
    public FileTouchImageView(Context ctx)
    {
        super(ctx);

    }
    public FileTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
    }

    public void setUrl(String imagePath)
    {
        new ImageLoadTask().execute(imagePath);
    }
    
    //No caching load
    public class ImageLoadTask extends UrlTouchImageView.ImageLoadTask
    {
    	
    	byte[] imageBuffer = null;
    	byte[] soundBuffer = null;
    	
        Bitmap bm = null;
        DataInputStream dis = null;
        
		@Override
        protected Bitmap doInBackground(String... strings) {
            String path = strings[0];

            SdcData data = new SdcData();
            data.setImageDataLisneter(new ImageDataListener() {
				
				@Override
				public void imageSize(long size) {

				}
				
				@Override
				public void imageData(byte[] data) {
//					bm = BitmapFactory.decodeByteArray(data, 0, (int) data.length );
					bm = getBitmap(data);
				}
			});

            data.getImageData(path);
            
            return bm;
        }
    }
    
	public Bitmap getBitmap(byte[] data) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length);
		
		final int REQUIRED_SIZE = 70;
		int width_tmp = o.outWidth;
		int height_tmp = o.outHeight;
		int scale = 1;
		
		while ( true ) {
			if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE) 
				break;
			width_tmp/=2;
			height_tmp/=2;
			scale*=2;
		}
		
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = scale;
		
		return BitmapFactory.decodeByteArray(data, 0, data.length, op);
	}
	
}
