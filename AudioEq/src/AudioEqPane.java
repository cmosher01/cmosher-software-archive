import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class AudioEqPane extends JPanel
{
	private int ifreq = Freq.iref;
	private final int freqRef = Freq.freq[Freq.iref];
	private OscillatorPlayer osc;

	private final JLabel currentFreq = new JLabel();
	private final JLabel refFreq = new JLabel();

	private final JButton buttonHi = new JButton("higher");
	private final JButton buttonLo = new JButton("lower");

	public AudioEqPane()
	{
		super(new BorderLayout(8,8));

		setBackground(Color.WHITE);
		setOpaque(false);
		addNotify();

		setPreferredSize(new Dimension(320,240));

		add(BorderLayout.CENTER,this.currentFreq);
		add(BorderLayout.SOUTH,this.refFreq);

		this.buttonLo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				loFreq();
			}
		});
		add(BorderLayout.WEST,this.buttonLo);

		this.buttonHi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				hiFreq();
			}
		});
		add(BorderLayout.EAST,this.buttonHi);

		this.refFreq.setText("reference: "+Integer.toString(getFreq())+" Hz");

		update();
	}

	private void hiFreq()
	{
		this.ifreq = Freq.next(this.ifreq);
		update();
	}

	private void loFreq()
	{
		this.ifreq = Freq.prev(this.ifreq);
		update();
	}

	private void update()
	{
		this.currentFreq.setText("current: "+Integer.toString(getFreq())+" Hz");
		if (this.osc != null)
		{
			this.osc.cancel();
		}
		this.osc = new OscillatorPlayer(getFreq(),this.freqRef);
	}

	public int getFreq()
	{
		return Freq.freq[this.ifreq];
	}
}
