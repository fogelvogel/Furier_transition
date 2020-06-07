import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class App {

	JFrame frame;
	public JPanel panel;

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1030, 547);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                int w = this.getWidth();
                int h = this.getHeight();
                g.setColor(Color.black);
                g.fillRect(0,0,w,h);
                //65536
                double[] arr = main.dftToPaint;
                if (arr == null)
                	return;
                g.setColor(Color.white);
                Graphics2D g2d = ((Graphics2D) g);
                Font font = new Font(getName(), 50, 70);
                g.setFont(font);
                g.drawString("the Fourier transform", 50, 200);
                g.setColor(Color.green);
                g2d.setStroke(new BasicStroke(4f));
                double max = arr[0];
                for (int i = 1; i < w; i++)
                	if (max < arr[i])
                		max = arr[i];
                double scale = h / max;
                for (int i = 5; i < w; i++)
                	if (arr[i] > 0) {
                		g.drawLine(i, 0, i, (int) (arr[i]*scale));
                		int tmp =  (int) (arr[i]*scale);
                		g.drawOval(600- tmp/2, 350 - tmp/2, tmp, tmp);
                	
                		
                	}
            }
		};
		panel.setBounds(10, 11, 1000, 486);
		frame.getContentPane().add(panel);
	}
}
