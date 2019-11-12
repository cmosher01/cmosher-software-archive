/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer;

import nu.mine.mosher.gedcom.viewer.file.FileManager;
import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;
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

        useUiNamed("nimbus", GedcomViewer::setUiDefaults);

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


    public void close() {
        // exit the application
        this.framer.close();
    }

    public void update(final Observable observable, final Object unused) {
        this.filer.updateMenu();
        this.framer.repaint();
    }



    private static void setUiDefaults(final UIDefaults defs) {
        defs.put("Tree.drawHorizontalLines", true);
        defs.put("Tree.drawVerticalLines", true);
        defs.put("Tree.linesStyle", "dashed");
        defs.put("Tree.rowHeight", 10);
        defs.put("Tree.font", new Font(Font.SANS_SERIF, Font.PLAIN, 8));
        defs.put("Tree.closedIcon", null);
        defs.put("Tree.openIcon", null);
        defs.put("Tree.leafIcon", null);
        defs.put("Tree.expandedIcon", new StringIcon("-", -1, 7, 16, 10));
        defs.put("Tree.collapsedIcon", new StringIcon("+", -2, 7, 16, 10));
    }

    private static void useUiNamed(final String name, final Consumer<UIDefaults> with) {
        try {
            final String className = getClassForUi(name);
            final LookAndFeel ui = (LookAndFeel)Class.forName(className).newInstance();
            with.accept(ui.getDefaults());
            UIManager.setLookAndFeel(ui);
        } catch (final Throwable e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (final Throwable e2) {
                throw new IllegalStateException(e2);
            }
        }
    }

    private static String getClassForUi(final String name) {
        for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (name.equalsIgnoreCase(info.getName())) {
                return info.getClassName();
            }
        }
        throw new IllegalStateException();
    }

    private void appendMenus(final JMenuBar bar) {
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        this.filer.appendMenuItems(menuFile);
        menuFile.addSeparator();
        appendMenuItems(menuFile);

        bar.add(menuFile);
    }

    private static final int ACCEL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private void appendMenuItems(final JMenu menu) {
        final JMenuItem itemFileExit = new JMenuItem("Quit");
        itemFileExit.setMnemonic(KeyEvent.VK_Q);
        itemFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ACCEL));
        itemFileExit.addActionListener(e -> close());
        menu.add(itemFileExit);
    }
}
