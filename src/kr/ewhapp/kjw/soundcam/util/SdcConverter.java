package kr.ewhapp.kjw.soundcam.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import kr.ewhapp.kjw.soundcam.main.SoundCamData;

import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils.ShellCallback;

import android.os.AsyncTask;
import android.util.Log;

public class SdcConverter {

	/**
	 * <code>RIFF</code> block identifier.
	 */
	private byte[] RIFF = toBytes("RIFF");
	/**
	 * <code>WAVE</code> block identifier.
	 */
	private byte[] WAVE = toBytes("WAVE");
	/**
	 * <code>fmt </code> block identifier.
	 */
	private byte[] FMT = toBytes("fmt ");
	/**
	 * <code>data</code> block identifier.
	 */
	private byte[] DATA = toBytes("data");

	private short numChannels = 1;
	private short bitPerSample = 16;
	private int sampleRateInHz = 11025;
	
	
	public void convertImageDataToJpg(String filePath, byte[] data) {
		Log.i("AAAAAAAAAAAAAAA", filePath);
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));
			dos.write(data);
			dos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void convertSoundDataToWave(String filePath, byte[] data) {

		try {
			
			final RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			file.setLength(data.length + 44);
			final FileChannel fc = file.getChannel();

			fc.position(44);

			ByteBuffer soundBuffer = ByteBuffer.allocateDirect(data.length);
			soundBuffer.put(data);
			soundBuffer.clear();
			fc.write(soundBuffer);

			ByteBuffer waveHeader = ByteBuffer.allocateDirect(44);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(RIFF);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(data.length + 36);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(WAVE);
			waveHeader.put(FMT);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(16);
			waveHeader.putShort((short) 1);
			waveHeader.putShort(numChannels);
			waveHeader.putInt(sampleRateInHz);
			waveHeader.putInt(sampleRateInHz * numChannels * bitPerSample / 8);
			waveHeader.putShort((short) (numChannels * bitPerSample / 8));
			waveHeader.putShort(bitPerSample);
			waveHeader.order(ByteOrder.BIG_ENDIAN);
			waveHeader.put(DATA);
			waveHeader.order(ByteOrder.LITTLE_ENDIAN);
			waveHeader.putInt(data.length);
			waveHeader.clear();

			fc.position(0);
			while (waveHeader.hasRemaining() && fc.isOpen()) {
				fc.write(waveHeader);
			}
			file.close();
			fc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void convertFileToVideo(byte[] imageData, byte[] soundData, String outputPath) {
		new ConvertTask(imageData, soundData, outputPath).execute();
	}
	
	private class ConvertTask extends AsyncTask<Void, Void, Void> {

		File tempDir;
		String tempImagePath;
		String tempSoundPath;
		String outputPath;
		
		byte[] imageData;
		byte[] soundData;
		
		public ConvertTask(byte[] imageData, byte[] soundData, String outputPath) {
			this.imageData = imageData;
			this.soundData = soundData;
			this.outputPath = outputPath;
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			tempDir = new File(SoundCamData.tempPath);
			tempDir.mkdir();
			tempImagePath = SoundCamData.tempPath + "temp.jpg";
			tempSoundPath = SoundCamData.tempPath + "temp.wav";

		}


		@Override
		protected Void doInBackground(Void... params) {
			
			convertImageDataToJpg(tempImagePath, imageData);
			convertSoundDataToWave(tempSoundPath, soundData);
			
			try {
				FfmpegController ffmpeg = new FfmpegController(new File("data/data/kr.ewhapp.soundcamera"));
				ffmpeg.convertAudioAndImageToVideo(new ShellCallback() {
					
					@Override
					public void shellOut(String shellLine) {
						Log.i("shellOut", shellLine);
					}
					
					@Override
					public void processComplete(int exitValue) {
						new File(tempImagePath).delete();
						new File(tempSoundPath).delete();
						
						Log.e("processComplete", "ExitValue : " + exitValue);
					}
				}, tempImagePath, tempSoundPath, outputPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			tempDir.delete();
		
		}
		
		
	}

	private static byte[] toBytes(String s) {
		try {
			return s.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			// unlikely to happen
			throw new IllegalStateException("ASCII encoding is not available", e);
		}
	}

}
