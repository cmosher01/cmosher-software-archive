/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.file;

import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.gui.exception.UserCancelled;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

//import nu.mine.mosher.charsets.GedcomAnselCharsetProvider;

public class FileManager {
    private JMenuItem itemFileOpen;
    private JMenuItem itemFileClose;

    private GedcomTreeModel model;
    private final FrameManager framer;

    private File file;

    public FileManager(final GedcomTreeModel model, final FrameManager framer) {
        this.model = model;
        this.framer = framer;
    }

    public void appendMenuItems(final JMenu appendTo) {
        this.itemFileOpen = new JMenuItem("Open\u2026");
        this.itemFileOpen.setMnemonic(KeyEvent.VK_O);
        this.itemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        this.itemFileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    fileOpen();
                } catch (final Throwable error) {
                    error.printStackTrace();
                }
            }
        });
        appendTo.add(this.itemFileOpen);

        this.itemFileClose = new JMenuItem("Close");
        this.itemFileClose.setMnemonic(KeyEvent.VK_C);
        this.itemFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        this.itemFileClose.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    fileClose();
                } catch (final Throwable error) {
                    error.printStackTrace();
                }
            }
        });
        appendTo.add(this.itemFileClose);
    }

    public void updateMenu() {
        this.itemFileOpen.setEnabled(true);
        this.itemFileClose.setEnabled(this.file != null);
    }



    //	private static GedcomAnselCharsetProvider ansel = new GedcomAnselCharsetProvider();

    private void fileOpen() {
        BufferedInputStream in = null;

        try {
            this.file = this.framer.getFileToOpen(this.file);
            // TODO detect character encoding
            in = new BufferedInputStream(new FileInputStream(this.file));
            this.model.open(in);
        } catch (final UserCancelled cancelled) {
            // user pressed the cancel button, so just return
        } catch (final Throwable e) {
            e.printStackTrace();
            this.framer.showMessage(e.toString());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Throwable eClose) {
                    eClose.printStackTrace();
                }
            }
        }
    }

    private void fileClose() {
        this.file = null;
        this.model.close();
    }
}
