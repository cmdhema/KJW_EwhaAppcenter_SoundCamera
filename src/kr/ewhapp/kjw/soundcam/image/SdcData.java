package kr.ewhapp.kjw.soundcam.image;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.util.Log;

/*
 *  **파일 구조**
 * 생성 일자
 * 사진 데이터 길이
 * 사진 데이터
 * 음성 데이터 길이
 * 음성 데이터
 */
public class SdcData {

	ImageDataListener imageListener;
	SoundDataListener soundListener;
	SdcDataListener sdcListener;
	
	public void getSdcData( String path ) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(path));
//			String filePath = dis.readUTF();
			long date = dis.readLong();
//			long imageSize = dis.readLong();
//			byte[] imageData = new byte[(int) imageSize];
//			dis.read(imageData, 0, (int)imageSize);
//			long soundSize = dis.readLong();
//			byte[] soundData;
//			if ( soundSize != -1 ) { 
//				 soundData = new byte[(int) soundSize];
//				dis.read(soundData, 0, (int) soundSize);
//			} else {
//				soundData = new byte[2];
//				soundData.
//			}
			sdcListener.fileDate(date);
//			sdcListener.fileData(date, imageSize, imageData, soundSize, soundData);
			dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void getImageData(String path) {
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(path));
//			dis.readUTF();
			long date = dis.readLong();
			Log.d("SdcData", "Date : " + date);
			long imageSize = dis.readLong();
			Log.d("SdcData", "ImageSize : " + imageSize);
			byte[] imageData = new byte[(int) imageSize];
			dis.read(imageData, 0, (int)imageSize);
			
			dis.close();

			imageListener.imageSize(imageSize);
			imageListener.imageData(imageData);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getSoundData(String path) {
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(path));
//			dis.readUTF();
			dis.readLong();
			long imageSize = dis.readLong();
			byte[] imageData = new byte[(int) imageSize];
			dis.read(imageData, 0, (int)imageSize);
			long soundSize = dis.readLong();
			soundListener.soundSize(soundSize);
			
			if ( soundSize != -1 ) {
				byte[] soundData = new byte[(int) soundSize];
				dis.read(soundData, 0, (int) soundSize);	
				soundListener.soundData(soundData);		
			}

			dis.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
	
	public void setImageDataLisneter ( ImageDataListener listener ) {
		this.imageListener = listener;
	}
	
	public void setSoundDataLisneter ( SoundDataListener listener ) {
		this.soundListener = listener;
	}
	
	public void setSdcDataLisneter ( SdcDataListener listener ) {
		this.sdcListener = listener;
	}
	
	
	public interface ImageDataListener {
		public void imageSize(long size);
		public void imageData(byte[] data);
	}
	
	public interface SoundDataListener {
		public void soundSize(long size);
		public void soundData(byte[] data);
	}
	
	public interface SdcDataListener {
		public void fileDate(long date);
//		public void fileData(long date, long imageSize, byte[] imageData, long soundSize, byte[] soundData);
	}
	
}

