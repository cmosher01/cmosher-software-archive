package gui;

import java.io.Closeable;
import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import chipset.Slots;

public class GUI {
//	private final JFrame frame;
//	private ContentPane contentPane;

	final Closeable app;
//	final Screen screen;
//	final ComputerControlPanel compControls;
//	final MonitorControlPanel monitorControls;
	final Slots slots;

	public GUI(final Closeable app, /*final Screen screen, final ComputerControlPanel compControls,
			final MonitorControlPanel monitorControls,*/ final Slots slots) {
		this.app = app;
//		this.screen = screen;
//		this.compControls = compControls;
//		this.monitorControls = monitorControls;
		this.slots = slots;
	}


//		this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		this.frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(@SuppressWarnings("unused") final WindowEvent e) {
//				try {
//					verifyLoseUnsavedChanges();
//				} catch (UserCancelled e2) {
//					return;
//				}
//
//				/*
//				 * When the user closes the main frame, we exit the application. To do
//				 * this, we dispose the frame (which ends the event dispatch thread),
//				 * then we tell the app to close itself.
//				 */
//				final Thread th = new Thread(new Runnable() {
//					@SuppressWarnings("synthetic-access")
//					public void run() {
//						GUI.this.frame.dispose();
//					}
//				});
//				th.setName("User-GUI-dispose frame");
//				th.setDaemon(true);
//				th.start();
//				try {
//					app.close();
//				} catch (final IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});

//		this.frame.setIconImage(getFrameIcon());

//		this.frame.setTitle("Apple ][");

		// this.frame.setJMenuBar(factoryMenuBar.createMenuBar());

		// Create and set up the content pane.

//	public void verifyLoseUnsavedChanges() throws UserCancelled {
//		if (this.contentPane.hasUnsavedChanges()) {
//			askLoseUnsavedChanges();
//		}
//	}

//	public void askLoseUnsavedChanges() throws UserCancelled {
//		if (!askOK("There are unsaved disk changes that will be LOST. Is it OK to DISCARD all disk changes?")) {
//			throw new UserCancelled();
//		}
//	}

	public File getFileToOpen(final File initial) throws UserCancelled {
//		final JFileChooser chooser = new JFileChooser(initial);
//		final int actionType = chooser.showOpenDialog(this.frame);
//		if (actionType != JFileChooser.APPROVE_OPTION) {
//			throw new UserCancelled();
//		}
//
//		return chooser.getSelectedFile();
		// TODO
		return new File("fix me");
	}

	public File getFileToSave(final File initial) throws UserCancelled {
//		final JFileChooser chooser = new JFileChooser(initial);
//		final int actionType = chooser.showSaveDialog(this.frame);
//		if (actionType != JFileChooser.APPROVE_OPTION) {
//			throw new UserCancelled();
//		}
//
//		return chooser.getSelectedFile();
		// TODO
		return new File("fix me");
	}

	public void showMessage(final String message) {
//		JOptionPane.showMessageDialog(this.frame, message);
		// TODO
	}

	public boolean askOK(final String message) {
//		final int choice = JOptionPane.showConfirmDialog(this.frame, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
//		return choice == JOptionPane.OK_OPTION;
		// TODO
		return true;
	}
//
//	public void toFront() {
//		this.frame.toFront();
//	}
}
