/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import nu.mine.mosher.sudoku.check.CheckerManager;
import nu.mine.mosher.sudoku.file.FileManager;
import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.solve.SolverManager;
import nu.mine.mosher.sudoku.state.GameManager;



/**
 * The main GUI for the whole program.
 *
 * @author Chris Mosher
 */
public class HowardGarns implements Runnable, Closeable
{
    /**
     * Main program entry point. Instantiate a HowardGarns object (on the
     * main thread) and runs it on Swing's event dispatch thread.
     * @param args
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException
    {
    	final HowardGarns howardgarns = new HowardGarns();

    	SwingUtilities.invokeAndWait(howardgarns);
    }



    private final GameManager game = new GameManager();
	private final FrameManager framer = new FrameManager(this.game);
	private final FileManager filer = new FileManager(this.game,this.framer);
	private final SolverManager solver = new SolverManager(this.game);
	private final CheckerManager checker = new CheckerManager(this.game,this.framer);



    private HowardGarns()
    {
    	// instantiated by the main static method, only
    	// Don't do any Swing stuff in this constructor
    }



    /**
	 * Runs the application.
	 */
	public void run()
	{
		FrameManager.setUpSwingDefaults();

        // create the application's menu bar
		final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);

        // create the main frame window for the application
        this.framer.init(menubar,new WindowAdapter()
    	{
			@Override
			public void windowClosing(final WindowEvent e)
			{
				close();
			}
    	});

		updateGameChange();

		this.game.addObserver(new Observer()
		{
			public void update(final Observable observableThatChagned, final Object typeOfChange)
			{
				updateGameChange();
			}
		});
	}

	private void updateGameChange()
	{
		this.game.updateMenu();
		this.filer.updateMenu();
		this.solver.updateMenu();
		this.checker.updateMenu();

		this.framer.repaint();
	}

    private void appendMenus(final JMenuBar bar)
	{
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);

		this.filer.appendMenuItems(menuFile);
		menuFile.addSeparator();
		appendMenuItems(menuFile);

		bar.add(menuFile);



		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		this.game.appendMenuItems(menuEdit);
		bar.add(menuEdit);



		final JMenu menuSolve = new JMenu("Solve");
		menuSolve.setMnemonic(KeyEvent.VK_S);
		this.solver.appendMenuItems(menuSolve);
		bar.add(menuSolve);



		final JMenu menuCheck = new JMenu("Check");
		menuCheck.setMnemonic(KeyEvent.VK_C);
		this.checker.appendMenuItems(menuCheck);
		bar.add(menuCheck);
	}

	private void appendMenuItems(final JMenu menu)
	{
		final JMenuItem itemFileExit = new JMenuItem("Exit");
		itemFileExit.setMnemonic(KeyEvent.VK_X);
		itemFileExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				close();
			}
		});
		menu.add(itemFileExit);
	}

	public void close()
	{
		try
		{
			this.filer.verifyLoseUnsavedChanges();
			this.framer.close(); // this exits the app
		}
		catch (final UserCancelled e)
		{
			// there were unsaved changes, and the user
			// decided not to lose them, therefore we
			// don't want to exit the application
		}
	}
}
