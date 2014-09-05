package kr.ewhapp.kjw.soundcam.record;

import android.os.Environment;

public class RecordData {

	public static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundCamera";

	private final static int WAVE_CHANNEL_MONO = 1; // wav ���� ��� ������ ä�� �����
	private final static int HEADER_SIZE = 0x2c;
	private final static int RECORDER_BPP = 16;
	private final static int RECORDER_SAMPLERATE = 44100;
//	private final static int RECORDER_SAMPLERATE = 0xac44;

	public static byte[] getFileHeader( int soundLength ) {

		byte[] header = new byte[HEADER_SIZE];

		int totalDataLen = soundLength + 36;

		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * WAVE_CHANNEL_MONO / 8;

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = (byte) 1; // format = 1 (PCM���)
		header[21] = 0;
		header[22] = WAVE_CHANNEL_MONO;
		header[23] = 0;
		header[24] = (byte) (RECORDER_SAMPLERATE & 0xff);
		header[25] = (byte) ((RECORDER_SAMPLERATE >> 8) & 0xff);
		header[26] = (byte) ((RECORDER_SAMPLERATE >> 16) & 0xff);
		header[27] = (byte) ((RECORDER_SAMPLERATE >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) RECORDER_BPP * WAVE_CHANNEL_MONO / 8; // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (soundLength & 0xff);
		header[41] = (byte) ((soundLength >> 8) & 0xff);
		header[42] = (byte) ((soundLength >> 16) & 0xff);
		header[43] = (byte) ((soundLength >> 24) & 0xff);

		return header;

	}
}
