package ftg;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * WildWest - � 2011 Contains everything for the sound effects
 * @author Thomas Lillywhite
 * 
 */
public class Sound extends Thread implements Runnable {
	
	//Music 
	static Sound themeMusic = new Sound((WildWest.class.getResource("Ballad of Serenity.wav")), false, true);
	static Sound backgroundMusic = new Sound((WildWest.class.getResource("Big Bar Fight.wav")), false, true);
	static Sound backgroundMusic2 = new Sound((WildWest.class.getResource("reavers.wav")), false, true);
	static Sound backgroundMusic3= new Sound((WildWest.class.getResource("reavers.wav")), false, true);
	//Sound Effects
	static Sound freeze = new Sound((WildWest.class.getResource("freeze.wav")), false, false);
	static Sound shield = new Sound((WildWest.class.getResource("shield chargeup.wav")), false, false);
	static Sound sonar = new Sound((WildWest.class.getResource("sonar.wav")), false, false);
	static Sound boost = new Sound((WildWest.class.getResource("boost.wav")), false, false);
	static Sound crash = new Sound((WildWest.class.getResource("crash.wav")), false, false);
	static Sound reavers = new Sound((WildWest.class.getResource("Reaver sound.wav")), false, false);
	

	/**
	 * @param url - path to file
	 * @param stop - if the sound needs to stop
	 * @param sync - if the sounds run() is synchronised or not
	 */
	public Sound(URL url, Boolean stop, Boolean sync) {
		this.url = url;
		this.stop = stop;
		this.sync = sync;
		

	}

	URL url;
	Boolean stop;
	Boolean sync;
	
	


	/**
	 *Method to stream an audio file
	 * 
	 */
	public void streamAudio(URL url) throws Exception {

		SourceDataLine soundLine = null;
		int BUFFER_SIZE = 64 * 1024; // 64 KB
		this.url = url;
		stop = false; 


		try {
			
			stop = false;
			this.url = url;
			
			AudioInputStream ais = AudioSystem.getAudioInputStream(url);  
			AudioFormat af = ais.getFormat();
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
			soundLine = (SourceDataLine) AudioSystem.getLine(info);
			soundLine.open(af);
			soundLine.start();
			
			int nBytesRead = 0;
			byte[] sampledData = new byte[BUFFER_SIZE];
			while (nBytesRead != -1 && !stop) {
				nBytesRead = ais.read(sampledData, 0, sampledData.length);
				if (nBytesRead >= 0) {
					// Writes audio data to the mixer via this source data line.
					soundLine.write(sampledData, 0, nBytesRead);
				}
			}
			
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		
		} catch (IOException ex) {
			ex.printStackTrace();
		
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		
		} finally {
			soundLine.drain();
			soundLine.close();
		}

	}

	/* 
	 * Overrides run, checks if the sound is a sync sound or not
	 */
	@Override
	public void run() {
		
		
		
		if(sync){
			synchronized (Sound.class){
			try {
				streamAudio(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		}
		else{
			try {
				streamAudio(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stops the sound playing
	 */
	public void stopAudio() {
		stop = true;
		
	}
	
	/**
	 * Plays the sound in a thread
	 */
	public static void playAudio(Sound s) {
		Thread t = new Thread(s);
		t.start();
		
	}
	
}