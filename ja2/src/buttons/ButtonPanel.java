/*
 * Created on Sep 18, 2007
 */
package buttons;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel
{
	private volatile String file;
	private volatile int track;
	private volatile boolean modified;
	private volatile boolean reading;
	private volatile boolean writing;

	private JLabel labelTrack;
	HiliteButton btnLoad;
	HiliteButton btnSave;

	public ButtonPanel()
	{
		setOpaque(true);
		setPreferredSize(new Dimension(640,480));
		addNotify();

		setupControls();
	}

	private void setupControls()
	{
		this.labelTrack = new JLabel();
		add(this.labelTrack);

		this.btnLoad = new HiliteButton("unload",50,12);
		add(this.btnLoad);
		this.btnSave = new HiliteButton("save",30,12);
		add(this.btnSave);
	}

	public void update()
	{
		this.labelTrack.setText(Integer.toHexString(this.track));
		this.btnSave.setEnabled(this.modified);
		this.btnLoad.setText(this.file == null ? "load" : "unload");
		repaint();
	}

	public void test1()
	{
		this.track = 17;
		this.modified = true;
		this.file = "master.nib";
	}

	public void test2()
	{
		this.track = 0;
		this.modified = false;
		this.file = null;
	}

	public void test(boolean on)
	{
		if (on)
		{
			test1();
		}
		else
		{
			test2();
		}
		update();
	}
}
