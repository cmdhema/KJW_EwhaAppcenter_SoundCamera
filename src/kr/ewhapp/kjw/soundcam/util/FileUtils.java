package kr.ewhapp.kjw.soundcam.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.ewhapp.kjw.soundcam.image.SdcData;
import kr.ewhapp.kjw.soundcam.image.SdcData.ImageDataListener;
import kr.ewhapp.kjw.soundcam.image.SdcData.SdcDataListener;
import kr.ewhapp.kjw.soundcam.image.SdcData.SoundDataListener;
import kr.ewhapp.kjw.soundcam.main.SoundCamData;
import android.util.Log;

public class FileUtils {

	private static String dirName = null;
	private static ArrayList<String> folderFilePathList = null;
	public static String lastFilePath;
	private static long lastDate;

	public static void getFileList(String source) {

		File dir = new File(source);
		File[] fileList = dir.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];

			if (file.isDirectory()) {
				if (dirName != null)
					SoundCamData.getInstance().getSdcFileListMap().put(dirName, folderFilePathList);

				dirName = file.getName();
				folderFilePathList = new ArrayList<String>();
				getFileList(file.getAbsolutePath());

			} else if (file.isFile()) {

				if (file.getAbsolutePath().endsWith("sdc")) {
					folderFilePathList.add(file.getAbsolutePath());
					if (i == fileList.length - 1)
						SoundCamData.getInstance().getSdcFileListMap().put(dirName, folderFilePathList);

					if (file.lastModified() > lastDate) {
						lastDate = file.lastModified();
						lastFilePath = file.getAbsolutePath();
					}

				}
			}
		}
	}
	
	public static ArrayList<String> getFolderNameList(HashMap<String, ArrayList<String>> fileMap) {
		
		Set<String> keyList = fileMap.keySet();
		Iterator<String> it = keyList.iterator();
		ArrayList<String> folderNameList = new ArrayList<String>();
		
		while ( it.hasNext() ) {
			String folderName = it.next();
			folderNameList.add(folderName);
		}
		
		return folderNameList;
	}

	public static long getFileName(String filePath) {
		String[] list = filePath.split("/");
		return Long.parseLong(list[list.length - 1].substring(0, list[list.length - 1].length() - 4));
	}

	public static boolean removeFile(String filePath) {
		File file = new File(filePath);
		if (file.delete())
			return true;
		else
			return false;
	}

	public static boolean moveFiles(final String filePath, String moveFolderPath) {

		try {
			final DataOutputStream dos = new DataOutputStream(new FileOutputStream(moveFolderPath + getFileName(filePath) + ".sdc"));
			DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
			Log.i("FileUtils", "이전 폴더 : " + filePath + "\n" + "이동 폴더 : " + moveFolderPath + getFileName(filePath));
//			byte data = 0;

			SdcData sdcData = new SdcData();
			sdcData.setSdcDataLisneter(new SdcDataListener() {
				
				@Override
				public void fileDate(long date) {
					try {
						dos.writeLong(date);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
//				@Override
//				public void fileData(long date, long imageSize, byte[] imageData, long soundSize, byte[] soundData) {
//					try {
//						dos.writeLong(date);
//						dos.writeLong(imageSize);
//						dos.write(imageData);
//						dos.writeLong(soundSize);
//						dos.write(soundData);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//
//				}
			});
			
			sdcData.setImageDataLisneter(new ImageDataListener() {
				
				@Override
				public void imageSize(long size) {
					try {
						dos.writeLong(size);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void imageData(byte[] data) {
					try {
						dos.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			sdcData.setSoundDataLisneter(new SoundDataListener() {
				
				@Override
				public void soundSize(long size) {
					try {
						dos.writeLong(size);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void soundData(byte[] data) {
					try {
						if ( data!= null)
							dos.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			sdcData.getSdcData(filePath);
			sdcData.getImageData(filePath);
			sdcData.getSoundData(filePath);
			removeFile(filePath);
			dis.close();
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}
}
