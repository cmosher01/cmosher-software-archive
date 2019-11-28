/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.file;



import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import static nu.mine.mosher.gedcom.viewer.GedcomViewer.ACCEL;


public class FileManager {
    private JMenuItem itemFileOpen;
    private JMenuItem itemFileClose;

    private final GedcomTreeModel model;
    private final FrameManager framer;

    public FileManager(final GedcomTreeModel model, final FrameManager framer) {
        this.model = model;
        this.framer = framer;
    }

    public void appendMenuItems(final JMenu appendTo) {
        this.itemFileOpen = new JMenuItem("Open\u2026");
        this.itemFileOpen.setMnemonic(KeyEvent.VK_O);
        this.itemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ACCEL));
        this.itemFileOpen.addActionListener(e -> fileOpen());
        appendTo.add(this.itemFileOpen);

        this.itemFileClose = new JMenuItem("Close");
        this.itemFileClose.setMnemonic(KeyEvent.VK_C);
        this.itemFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ACCEL));
        this.itemFileClose.addActionListener(e -> fileClose());
        appendTo.add(this.itemFileClose);
    }

    public void updateMenu() {
        this.itemFileOpen.setEnabled(true);
        this.itemFileClose.setEnabled(Objects.nonNull(this.model.getRoot()));
    }



    private void fileOpen() {
        BufferedInputStream in = null;
        try {
            final File file = this.framer.getFileToOpen();
            in = new BufferedInputStream(new FileInputStream(file));
            this.model.open(in);
        } catch (final FrameManager.UserCancelled cancelled) {
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
        this.model.close();
    }
}
