//import java.io.BufferedInputStream;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class main {
	public static double[] dftToPaint = null;
	public static App window;
	
	public static int Fs;
	public static int frameSize = 1000;
	
	private static void playClip(byte[] arr, AudioFormat f, long size) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      Type eventType = event.getType();
	      if (eventType == Type.STOP || eventType == Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
		AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(arr), f, size);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}
	
	public static void computeDft(int k, double[] inreal, double[] outreal) {
		int n = inreal.length;
		double sumreal = 0;
		double sumimag = 0;
		for (int t = 0; t < n; t++) {
			double angle = 2 * Math.PI * t * k / n;
			sumreal +=  inreal[t] * Math.cos(angle);
			sumimag += -inreal[t] * Math.sin(angle);
		}
		//outreal[k]
		outreal[k] = Math.sqrt(sumreal*sumreal + sumimag*sumimag);
//		System.out.println(k*Fs/n);
//		System.out.println(magnitudes[k]);
	}
	
	public static void start() throws IOException, LineUnavailableException  {
		 Path path = Paths.get("res","2.mp3");
		 ArrayList intArr = new ArrayList();
		 ArrayList byteArr = new ArrayList();
		 try(Sound sound = new Sound(new BufferedInputStream(Files.newInputStream(path)))) {
			 Fs = sound.getSamplingFrequency();
			 byte[] b;
			 while (true) {
				 int b1 = sound.read();
				 int b2 = sound.read();
				 byteArr.add((byte)b1);
				 byteArr.add((byte)b2);
				int ref = b1;
				if (ref == -1)
					break;
		        ref |= b2 << 8;
		        intArr.add(ref);
			 }
			 int intSize = intArr.size();
//			 int intSize = 1000;
//			 int byteSize = byteArr.size();
			 int byteSize = intSize*2;
			 int[] ampl = new int[intSize];
			 byte[] byteSound = new byte[byteSize];
			 
			for (int i = 0; i < intSize; i++) {
				ampl[i] = (int)intArr.get(i);
				byteSound[i] = (byte)byteArr.get(i);
				byteSound[i + intSize] = (byte)byteArr.get(i + intSize);
			}
			int frameOffset = 0;
			double[] frame = null;
			boolean done = false;
			while (!done) {
				frame = new double[frameSize];
				for (int i = 0; i < frameSize; i++) {
					if (i + frameOffset < intSize) {
						frame[i] = ampl[i + frameOffset];
					}
					else {
						done = true;
						break;
					}
				}

				dftToPaint = new double[frameSize];
				
				for (int k = 0; k < frameSize; k+=15) {
					computeDft(k, frame, dftToPaint);
					window.panel.repaint();
				}

				frameOffset += frameSize;
			}
			sound.close();
			
			/*new Thread(new Runnable() {
			    public void run() {
					try {
						playClip(byteSound, sound.getAudioFormat(), intSize);
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}).start();*/
			
//			new Thread(new Runnable() {
//			    public void run() {
//					for (int k = 0; k < intSize; k++) {
//						computeDft(k, dftInp, dftToPaint);
//						window.panel.repaint();
//					}
//			    }
//			}).start();
		 }
		 }

	public static void main(String[] args) throws IOException, LineUnavailableException {
		window = new App();
		window.frame.setVisible(true);
		while (true) {
			start();
		}
	}
}
