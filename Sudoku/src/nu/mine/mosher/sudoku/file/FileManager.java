/*
 * Created on Oct 18, 2005
 */
package nu.mine.mosher.sudoku.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import nu.mine.mosher.sudoku.check.CheckerManager;
import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.solve.SolverManager;
import nu.mine.mosher.sudoku.state.GameManager;



public class FileManager
{
	private JMenuItem itemFileNew;
	private JMenuItem itemFileOpen;
	private JMenuItem itemFileSave;
	private JMenuItem itemFileSaveAs;

	private final GameManager game;
	private final FrameManager framer;

	private File file;
	private GameManager gameLastSaved;



	public FileManager(final GameManager game, final FrameManager framer)
	{
		this.game = game;
		this.framer = framer;
		this.gameLastSaved = (GameManager)this.game.clone();
	}

	public void appendMenuItems(final JMenu appendTo)
	{
		this.itemFileNew = new JMenuItem("New");
		this.itemFileNew.setMnemonic(KeyEvent.VK_N);
		this.itemFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		this.itemFileNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					fileNew();
				}
				catch (final Throwable error)
				{
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileNew);

		this.itemFileOpen = new JMenuItem("Open\u2026");
		this.itemFileOpen.setMnemonic(KeyEvent.VK_O);
		this.itemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		this.itemFileOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					fileOpen();
				}
				catch (final Throwable error)
				{
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileOpen);

		this.itemFileSave = new JMenuItem("Save");
		this.itemFileSave.setMnemonic(KeyEvent.VK_S);
		this.itemFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		this.itemFileSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					fileSave();
				}
				catch (final Throwable error)
				{
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileSave);

		this.itemFileSaveAs = new JMenuItem("Save As\u2026");
		this.itemFileSaveAs.setMnemonic(KeyEvent.VK_A);
		this.itemFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		this.itemFileSaveAs.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					fileSaveAs();
				}
				catch (final Throwable error)
				{
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileSaveAs);
	}

	public void updateMenu()
	{
		this.itemFileNew.setEnabled(true);
		this.itemFileOpen.setEnabled(true);
		this.itemFileSave.setEnabled(this.file != null);
		this.itemFileSaveAs.setEnabled(true);
	}



	private void fileSaveAs()
	{
		try
		{
			this.file = this.framer.getFileToSave(this.file);
			fileSave();
		}
		catch (final UserCancelled cancelled)
		{
			// user pressed the cancel button, so just return
		}
	}

	private void fileSave()
	{
		BufferedWriter out = null;
	    try
		{
	    	out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file),"UTF-8"));
		    this.game.write(out);
		    this.gameLastSaved = (GameManager)this.game.clone();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			this.framer.showMessage(e.getLocalizedMessage());
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (final Throwable eClose)
				{
					eClose.printStackTrace();
				}
			}
		}
	}

	private void fileOpen()
	{
		BufferedReader in = null;

		try
		{
			verifyLoseUnsavedChanges();
			this.file = this.framer.getFileToOpen(this.file);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(this.file),"UTF-8"));
			this.game.read(in);
		    this.gameLastSaved = (GameManager)this.game.clone();
			// TODO upon read-in, do brute force solve to count the number of possible solutions (there should be one and only one).
		}
		catch (final UserCancelled cancelled)
		{
			// user pressed the cancel button, so just return
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			this.framer.showMessage(e.getLocalizedMessage());
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (final Throwable eClose)
				{
					eClose.printStackTrace();
				}
			}
		}
	}

	private void fileNew()
	{
		try
		{
			verifyLoseUnsavedChanges();
			final String sBoard = this.framer.getBoardStringFromUser();
			this.file = null;
			this.game.read(sBoard);
			this.gameLastSaved = (GameManager)this.game.clone();
			// TODO upon new entry by user, do brute force solve to count the number of possible solutions (there should be one and only one, otherwise allow user the option to re-enter).
		}
		catch (UserCancelled e)
		{
			// user pressed the cancel button, so just return
		}
	}

	public void verifyLoseUnsavedChanges() throws UserCancelled
	{
		if (this.game.equals(this.gameLastSaved))
		{
			return;
		}

		if (!this.framer.askOK("Your current game will be DISCARDED. Is this OK?"))
		{
			throw new UserCancelled();
		}
	}

	public int countSolutions()
	{
		final GameManager gameCopy = (GameManager)this.game.clone();
		final SolverManager solver = new SolverManager(gameCopy);
		solver.appendMenuItems(new JMenu());
		solver.solve();

		// now the game is in a (possibly) unsolved state
		// so from here we check all possible answers and see
		// how many of them are correct

		CheckerManager checker = new CheckerManager(gameCopy,null);
		int cSolution = 0;
		if (checker.isCorrect())
		{
			++cSolution;
		}
		// TODO brute force solve here
		return cSolution;
	}
}
