package gui;

import java.awt.dnd.DropTargetListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import cards.Card;
import chipset.Slots;

/*
 * Created on Sep 19, 2007
 */
class ContentPane extends JPanel
{
	private final Screen screen;
	private final ComputerControlPanel compControls;
	private final MonitorControlPanel monitorControls;
	private final Slots slots;
	private final DropTargetListener dropTarget;



	public ContentPane(final Screen screen, final ComputerControlPanel compControls, final MonitorControlPanel monitorControls, final Slots slots, final GUI gui)
	{
		this.screen = screen;
		this.compControls = compControls;
		this.monitorControls = monitorControls;
		this.slots = slots;
		setOpaque(true);
		addNotify();

		this.dropTarget = setUp(gui);
	}

	private DropTargetListener setUp(final GUI gui)
	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		setBorder(BorderFactory.createLoweredBevelBorder());

		add(this.screen);

		add(this.monitorControls);
		add(this.compControls);

		DropTargetListener drop = null;
		int slot = 0;
		for (final Card card : this.slots)
		{
			final JPanel cardPanel = card.getPanel(gui);
			if (cardPanel != null)
			{
				cardPanel.setBorder(BorderFactory.createTitledBorder("slot "+slot));
				add(cardPanel);
				final DropTargetListener dropCard = card.getDropListener();
				if (dropCard != null)
				{
					drop = dropCard;
				}
			}
			++slot;
		}
		return drop;
	}

	public DropTargetListener getFirstDrivePanelDropListener()
	{
		return this.dropTarget;
	}

	public void updateScreen()
	{
		this.screen.plot();
	}

	public boolean hasUnsavedChanges()
	{
		return this.slots.isAnyDiskDirty();
	}
}
