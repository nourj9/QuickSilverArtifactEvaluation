package utils;

//credits: https://stackoverflow.com/questions/13008526/runtime-getruntime-execcmd-hanging

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadStream implements Runnable {
	String name;
	InputStream is;
	Thread thread;
	File logFile;
	BufferedWriter writer;

	public ReadStream(String name, InputStream is, File logFile) throws Exception {
		this.name = name;
		this.is = is;
		this.logFile = logFile;
		if (logFile != null) {
			logFile.createNewFile();
			writer = new BufferedWriter(new FileWriter(logFile, true));

		}
	}

	public ReadStream(String name, InputStream is) throws Exception {
		this(name, is, null);
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				if (logFile != null) {
					writer.write(s);
					writer.newLine();
				} else {
					System.out.println("[" + name + "] " + s);
				}
			}
			if (writer != null) {
				writer.flush();
				writer.close();
			}
			is.close();
		} catch (Exception ex) {
			System.out.println("Problem reading stream " + name + "... :" + ex);
			ex.printStackTrace();
		}
	}
}