/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer;

import nu.mine.mosher.gedcom.viewer.file.FileManager;
import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.prefs.Preferences;



public class GedcomViewer implements Runnable, Closeable, Observer {
    public static void main(final String... args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> new GedcomViewer().run());
    }

    public static Preferences prefs() {
        return Preferences.userNodeForPackage(GedcomViewer.class);
    }

    private final GedcomTreeModel model = new GedcomTreeModel();
    private final FrameManager framer = new FrameManager(this.model);
    private final FileManager filer = new FileManager(this.model, this.framer);

    private GedcomViewer() {
        // instantiated by main method
    }

    public void run() {
        this.model.addObserver(this);

        setUpUi();

        // Use look and feel's (not OS's) decorations.
        // Must be done before creating any JFrame or JDialog
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        // create the application's menu bar
        final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);

        this.framer.init(menubar, this::close);

        update(this.model, null);
    }

    private void setUpUi() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (final Throwable e2) {
            // OK
        }
    }

    public void close() {
        // exit the application
        this.framer.close();
    }

    public void update(final Observable observable, final Object unused) {
        this.filer.updateMenu();
    }

    private void appendMenus(final JMenuBar bar) {
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        this.filer.appendMenuItems(menuFile);
        menuFile.addSeparator();
        appendFileMenuItems(menuFile);
        bar.add(menuFile);

        final JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        appendHelpMenuItems(menuHelp);
        bar.add(menuHelp);
    }

    private static final int ACCEL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private void appendFileMenuItems(final JMenu menu) {
        final JMenuItem itemFileExit = new JMenuItem("Quit");
        itemFileExit.setMnemonic(KeyEvent.VK_Q);
        itemFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ACCEL));
        itemFileExit.addActionListener(e -> close());
        menu.add(itemFileExit);
    }

    private void appendHelpMenuItems(final JMenu menu) {
        final JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.setMnemonic(KeyEvent.VK_A);
        itemAbout.addActionListener(e -> about());
        menu.add(itemAbout);
    }

    private void about() {
        this.framer.showMessage(
            "<html>" +
            "<p style='font-size:22'>GEDCOM Viewer</p><br>" +
            "Copyright © 2005\u20132019, Christopher Alan Mosher, Shelton, Connecticut, USA<br>" +
            "https://github.com/cmosher01" +
            "</html>");
    }
}
