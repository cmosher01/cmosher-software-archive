/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.file;



import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Optional;



public class FileManager {
    private JMenuItem itemFileOpen;
    private JMenuItem itemFileClose;

    private final GedcomTreeModel model;
    private final FrameManager framer;

    private Optional<File> file = Optional.empty();

    public FileManager(final GedcomTreeModel model, final FrameManager framer) {
        this.model = model;
        this.framer = framer;
    }

    private static final int ACCEL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

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
        this.itemFileClose.setEnabled(this.file.isPresent());
    }



    //	private static GedcomAnselCharsetProvider ansel = new GedcomAnselCharsetProvider();

    private void fileOpen() {
        BufferedInputStream in = null;

        try {
            this.file = Optional.of(this.framer.getFileToOpen(this.file));
            // TODO detect character encoding
            in = new BufferedInputStream(new FileInputStream(this.file.get()));
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
        this.file = Optional.empty();
        this.model.close();
    }
}
