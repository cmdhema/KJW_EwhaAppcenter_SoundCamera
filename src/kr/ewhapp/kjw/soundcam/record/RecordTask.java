package kr.ewhapp.kjw.soundcam.record;

import java.util.ArrayList;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class RecordTask {

	//Record Variable
	public final int CUSTOM_FREQ_SOAP = 1;
	private int frequency = 11025;
	private int outfrequency = frequency * CUSTOM_FREQ_SOAP;
	private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord audioRecord;
	
	private OnRecordListener listener;
	private static RecordTask instance;

	private boolean isRecording = false;
	private boolean isPlaying = false;
	private boolean isStop = false;
	
	private long recordDataSize;
	
	public static final int INIT = 99;
	public static final int PLAYING = 100;
	public static final int STOP = 101;
	public static final int PAUSE = 102;
	public static final int RECORDING = 103;

	private int status = INIT;

	private ArrayList<LinkedList<Byte>> recordedQueue;
	
	public RecordTask() {

	}
	
	public static RecordTask getInstance() {
		if ( instance == null )
			instance = new RecordTask();
		
		return instance;
	}
	
	public void startRecord() {
		isRecording = true;
		status = RECORDING;
		new RecordSound().execute();
	}
	
	public void pauseRecord() {
		isRecording = false;
		status = PAUSE;
	}
	
	public void resumeRecord() {
		isRecording = true;
		status = RECORDING;
		new RecordSound().execute();
	}
	
	public void stopRecord() {
		status = STOP;
		isRecording = false;
		isStop = true;
	}
	
	public void startPlay(byte[] soundData) {
		new PlaySound().execute(soundData);
	}
	
	public void setOnRecordListener(OnRecordListener listener) {
		this.listener = listener;
	}
	
	public interface OnRecordListener {
		public void onSuccess(ArrayList<LinkedList<Byte>> list, long size);
		public void onError();
	}
	
	private class RecordSound extends AsyncTask<Void, Integer, Boolean> {

		LinkedList<Byte> recordDataList;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);

			byte[] buffer = new byte[bufferSize];
			audioRecord.startRecording();

			recordDataList = new LinkedList<Byte>();
			
			if ( recordedQueue == null )
				recordedQueue = new ArrayList<LinkedList<Byte>>();
			
			while (isRecording) {
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
				recordDataSize += bufferReadResult;

				for (int i = 0; i < bufferReadResult; i++) {
					recordDataList.add(buffer[i]);
				}
			}

			recordedQueue.add(recordDataList);
			
			audioRecord.stop();
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if ( status == STOP ) {
				
				if ( listener != null ) 
					listener.onSuccess(recordedQueue, recordDataSize);

				recordedQueue = null;
				recordDataSize = 0;
			} 
		}
	}
	
	private class PlaySound extends AsyncTask<byte[], Integer, Boolean> {

		@Override
		protected Boolean doInBackground(byte[]... params) {
			isPlaying = true;
			
			byte[] audioData = params[0];
			int bufferSize = AudioTrack.getMinBufferSize(outfrequency, channelConfiguration, audioEncoding);

			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, outfrequency, AudioFormat.CHANNEL_OUT_MONO, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
			audioTrack.play();

			Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", audioData.length+"dddddd");
			audioTrack.write(audioData, 0, audioData.length);
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ( result ) {
				isPlaying = false;
			} else {
				
			}
		}
	}
	

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
