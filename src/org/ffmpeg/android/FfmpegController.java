package org.ffmpeg.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ffmpeg.android.ShellUtils.ShellCallback;

public class FfmpegController {

	private File mFileBinDir;
	private String mFfmpegBin;

	public FfmpegController(File fileAppRoot) throws FileNotFoundException, IOException {
		checkBinary(fileAppRoot);
	}

	private void checkBinary(File fileAppRoot) throws FileNotFoundException, IOException {
		mFileBinDir = new File(fileAppRoot, "lib");

		if (mFileBinDir.exists()) {
			File fileBin = new File(mFileBinDir, "libffmpeg.so");

			if (fileBin.exists())
				mFfmpegBin = fileBin.getCanonicalPath();
			else
				mFfmpegBin = "ffmpeg";

		} else {
			mFfmpegBin = "ffmpeg";
		}

	}

	private void execFFMPEG(List<String> cmd, ShellCallback sc, File fileExec) throws IOException, InterruptedException {

		String runtimeCmd = new File(mFileBinDir, "ffmpeg").getCanonicalPath();

		Runtime.getRuntime().exec("chmod 777 " + runtimeCmd);

		execProcess(cmd, sc, fileExec);
	}

	private void execFFMPEG(List<String> cmd, ShellCallback sc) throws IOException, InterruptedException {
		execFFMPEG(cmd, sc, mFileBinDir);
	}

	private int execProcess(List<String> cmds, ShellCallback sc, File fileExec) throws IOException, InterruptedException {

		// ensure that the arguments are in the correct Locale format
		for (String cmd : cmds) {
			cmd = String.format(Locale.US, "%s", cmd);
		}

		ProcessBuilder pb = new ProcessBuilder(cmds);
		pb.directory(fileExec);

		StringBuffer cmdlog = new StringBuffer();

		for (String cmd : cmds) {
			cmdlog.append(cmd);
			cmdlog.append(' ');
		}

		sc.shellOut(cmdlog.toString());

		// pb.redirectErrorStream(true);

		Process process = pb.start();

		// any error message?
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", sc);

		// any output?
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", sc);

		errorGobbler.start();
		outputGobbler.start();

		int exitVal = process.waitFor();

		sc.processComplete(exitVal);

		return exitVal;

	}

	public void convertAudioAndImageToVideo(ShellCallback sc, String imgPath, String audioPath, String outPath) throws IOException, InterruptedException {

		ArrayList<String> cmd = new ArrayList<String>();

		cmd = new ArrayList<String>();

		cmd.add(mFfmpegBin);
		cmd.add("-y");
		cmd.add("-loop");
		cmd.add("1");
		cmd.add("-r");
		cmd.add("1");
		cmd.add("-i");
		cmd.add(new File(imgPath).getCanonicalPath());
		cmd.add("-i");
		cmd.add(new File(audioPath).getCanonicalPath());
		cmd.add("-acodec");
		cmd.add("aac");
		cmd.add("-vcodec");
		cmd.add("mpeg4");
		cmd.add("-s");
		cmd.add("480x320");
		cmd.add("-strict");
		cmd.add("experimental");
		cmd.add("-b:a");
		cmd.add("32k");
		cmd.add("-shortest");
		cmd.add("-f");
		cmd.add("mp4");
		cmd.add("-r");
		cmd.add("2");
		File fileOut = new File(outPath);
		cmd.add(fileOut.getCanonicalPath());

		execFFMPEG(cmd, sc);
	}

	private class StreamGobbler extends Thread {
		InputStream is;
		String type;
		ShellCallback sc;

		StreamGobbler(InputStream is, String type, ShellCallback sc) {
			this.is = is;
			this.type = type;
			this.sc = sc;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					if (sc != null)
						sc.shellOut(line);

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
