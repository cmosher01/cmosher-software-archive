/*
 *  NoiseSphere.java - Derived from many sources.
 * 	Before java it was in C++ by Robert Rothenburg Walking-Owl.
 *	This version by Chuck McManis (cmcmanis@netcom.com)
 *	Date of this version 26-Mar-96
 */
import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.Random;

/**
 * Class to plot a noise sphere.
 *
 * This program reads a file of random or pseudo-random data and plots
 * a noise sphere of the data. Poor RNGs or sampling methods will show
 * clear patterns (definite splotches or spirals).
 *
 * The theory behind this is to get a set of 3D polar coordinates from
 * the RNG and plot them.  An array is kept of the values, which is
 * rotated each time a new byte is read (see the code in the main
 * procedure).
 *
 * Rather than plot one sphere which can be rotated around any axis,
 * it was easier to plot the sphere from three different angles.
 *
 * This program is based on a description from the article below.  It
 * was proposed as a means of testing pseudo-RNGs:
 *
 *  Pickover, Clifford A. 1995. "Random number generators: pretty good
 *	 ones are easy to find."  The Visual Computer (1005) 11:369-377.
 */
public class NoiseSphere extends Applet implements Runnable {
    int imageData[];
    Image sphereImage;
    Image altImage;
    Graphics offscreen;
    int Scale, MidA, MidB, MidC, MidY;
    int myWidth, myHeight;
    Font myFont = new Font("Helvetica", Font.BOLD+Font.ITALIC, 12);
    MemoryImageSource memSrc;
    boolean morePoints = true;
    int incrementalPoints = 1000;

    int getInteger(String p, int def) {
    	String x = getParameter(p);
    	if (x == null)
    	    return def;

    	try {
    	    return (Integer.parseInt(x));
    	} catch (NumberFormatException e) { return def; }
    }

    public void init() {
	myWidth = size().width;
	myHeight = size().height;
	imageData = new int[(int)(myWidth * myHeight * 1.1)];
	for (int i = 0; i < imageData.length; i++) imageData[i] = 0xff000000;
	memSrc = new MemoryImageSource(myWidth, myHeight, imageData, 0, myWidth);
	Scale = myWidth / 4;
	System.out.println("Initial Scale = "+Scale);
	MidA = Scale;
	MidB = (3 * Scale);
	incrementalPoints = getInteger("points", 1000);
    }

    final int round(double x) {
    	return ((int)(x + .5));
    }

    void plot(Cartesian c) {
    	try {
    	    imageData[(MidA + round(Scale * c.y)) * myWidth+

    			MidA - round(Scale * c.z)] = 0xffd0d0d0;
    	    imageData[(MidA + round(Scale * c.x)) * myWidth+
    			MidB - round(Scale * c.y)] = 0xffd0d0d0;
    	    imageData[(MidB + round(Scale * c.z)) * myWidth+
    			MidA - round(Scale * c.x)] = 0xffd0d0d0;
    	} catch (ArrayIndexOutOfBoundsException e) {
    	    System.out.println("plot() exiting with array index out of bounds.");
    	    System.out.println("Cartesian Values are : (x, y, z) ==> ("+c.x+", "+c.y+", "+c.z+")");
    	    System.out.println("    ... (W, H) ==> ("+myWidth+", "+myHeight+")");
    	    System.out.println("    ... (Scale, MidA, MidB) ==> ("+Scale+", "+MidA+", "+MidB+")");
    	    throw e;
    	}
    }

    int line(FontMetrics fm, int y, int n) {
    	return (n * (fm.getHeight() + fm.getLeading())+y);
    }

    public void paint(Graphics g) {
    	update(g);
    }

    public void update(Graphics g) {
    	FontMetrics fm;
    	int x = myWidth/2+20;
    	int y = MidB;

    	if (offscreen == null) {
    	    altImage = createImage(myWidth, myHeight);
    	    offscreen = altImage.getGraphics();
    	}
    	sphereImage = createImage(memSrc);
	offscreen.drawImage(sphereImage, 0, 0, null);
    	offscreen.setColor(Color.green);
    	offscreen.setFont(myFont);
    	fm = offscreen.getFontMetrics();
    	offscreen.drawString("GENERATORI RANDOM", x, line(fm, y, 0));
    /*	offscreen.drawString("generatori random", x, line(fm, y, 1)); */
    	offscreen.drawString("Punti disegnati : "+points, x, line(fm, y, 2));
    	offscreen.drawString("Clicca sulla finestra ",x, line(fm, y, 3));
    	offscreen.drawString("per altri "+incrementalPoints,x, line(fm, y, 4));
    	offscreen.drawString("punti.",x, line(fm, y, 5));
    	offscreen.drawString("Premere 'c' per ricominciare.",
    			x, line(fm, y, 6));
    	g.drawImage(altImage, 0, 0, null);
    }

    public boolean keyDown(Event ev, int key) {
    	if ((key == 'c') || (key == 'C')) {
    	    for (int i = 0; i < imageData.length; i++)
    	        imageData[i] = 0xff000000; // clear image
    	    points = 0;
    	    repaint();
    	}
    	return true;
    }

    Thread myThread;

    public void start() {
    	myThread = new Thread(this);
    	myThread.start();
    }

    public void stop() {
    	myThread = null;
    }

    public boolean mouseDown(Event ev, int x, int y) {
    	morePoints = true;
    	return true;
    }

    int points;	// total points plotted

    public void run() {
    	Random r = new Random();
    	byte x[] = new byte[3];
	Polar p = new Polar();
	int n = 0;

	for (int i = 0; i < 3; i++) {
	    x[i] = (byte) (r.nextInt());
	}

	while (myThread == Thread.currentThread()) {
	    if (! morePoints) {
	    	try {
	    	    myThread.sleep(250);
	    	} catch (InterruptedException e) { break; }
	    	continue;
	    }
	    points++;
	    p.update(x[(n+2)%3], x[(n+1) % 3], x[n]);
    	    plot(p.toCartesian());
    	    x[n] = (byte)(r.nextInt());
    	    n = (n + 1) % 3;
    	    if ((points % incrementalPoints)== 0) {
    	    	repaint();
    	    	morePoints = false;
    	    }
	}
    }
}

class Cartesian {
    double x, y, z;
    Color color;
}

class Polar {
    double radius, theta, phi;
    Cartesian ct = null;

    public void update(byte a, byte b, byte c) {
    	int a1 = ((int) a) & 0xff;
    	int b1 = ((int) b) & 0xff;
    	int c1 = ((int) c) & 0xff;
    	radius = Math.sqrt(a1 / 256.0);
    	theta = Math.PI * (b1 / 256.0);
    	phi = Math.PI * 2 * (c1 / 256.0);
    }

    public Cartesian toCartesian () {
	double x, y, z;
	Color  c;
	if (ct == null)
	    ct = new Cartesian();

	ct.x = radius * Math.sin(phi) * Math.cos(theta);
	ct.y = radius * Math.sin(phi) * Math.sin(theta);
	ct.z = radius * Math.cos(phi);
	/*
	 * We can assign colors based on x, y, z, r,
	 * theta / pi or phi / (2 * pi)
	 */
	ct.color = scaleColor(ct.y);
	return (ct);
    }

    Color scaleColor(double x) {
	return Color.lightGray;
    }
}
