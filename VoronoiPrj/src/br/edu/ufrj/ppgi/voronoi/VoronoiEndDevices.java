package br.edu.ufrj.ppgi.voronoi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class VoronoiEndDevices extends JFrame {
	static double p = 3;
	static BufferedImage I;
	static int px[], py[], color[], cells = 5, width=420, height = 400;
 
	public VoronoiEndDevices() {
		super("Voronoi Diagram");
		setBounds(0, 0, width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		int n = 0;
		I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		px = new int[cells];
		py = new int[cells];
		color = new int[cells];
		// Y vertical, X horizontal
		py[0] = 150; px[0] = 100;  //ed1
		py[1] = 150; px[2] = 180;  //ed2
		py[2] = 200; px[1] = 180;  //ed3
		py[3] = 200; px[3] = 380;  //ed4
		py[4] = 300; px[4] = 380;  //ed5
		
		color[0] = Color.BLUE.getRGB();
		color[1] = Color.CYAN.getRGB();
		color[2] = Color.GREEN.getRGB();
		color[3] = Color.ORANGE.getRGB();
		color[4] = Color.MAGENTA.getRGB();
//		for (int i = 0; i < cells; i++) {
//			px[i] = rand.nextInt(size);
//			py[i] = rand.nextInt(size);
//			color[i] = rand.nextInt(16777215);
//		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				n = 0;
				for (byte i = 0; i < cells; i++) {
					if (distance(px[i], x, py[i], y) < distance(px[n], x, py[n], y)) {
						n = i;
					}
				}
				I.setRGB(x, y, color[n]);
			}
		}
 
		Graphics2D g = I.createGraphics();
		g.setColor(Color.BLACK);
		for (int i = 0; i < cells; i++) {
			g.fill(new Ellipse2D .Double(px[i] - 2.5, py[i] - 2.5, 5, 5));
//			g.drawString("ed"+(i+1)+"=>"+Integer.toString(py[i])+":"+Integer.toString(px[i]), px[i]+5, py[i]);
			g.drawString("a"+(i+1), px[i], py[i]-15);
			g.drawString("ed"+(i+1), px[i]+5, py[i]+3);
		}
 
		try {
			ImageIO.write(I, "png", new File("voronoi_enddevice2.png"));
		} catch (IOException e) {
 
		}
 
	}
 
	public void paint(Graphics g) {
		g.drawImage(I, 0, 0, this);
	}
 
	static double distance(int x1, int x2, int y1, int y2) {
		double d;
	    d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); // Euclidian
	//  d = Math.abs(x1 - x2) + Math.abs(y1 - y2); // Manhattan
	//  d = Math.pow(Math.pow(Math.abs(x1 - x2), p) + Math.pow(Math.abs(y1 - y2), p), (1 / p)); // Minkovski
	  	return d;
	}
 
	public static void main(String[] args) {
		new VoronoiEndDevices().setVisible(true);
	}
}