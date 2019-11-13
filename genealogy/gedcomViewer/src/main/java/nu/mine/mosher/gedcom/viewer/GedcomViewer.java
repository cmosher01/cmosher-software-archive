/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer;

import nu.mine.mosher.gedcom.viewer.file.FileManager;
import nu.mine.mosher.gedcom.viewer.gui.FrameManager;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
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

    private int visualSize = prefZoom();

    private GedcomViewer() {
        // instantiated by main method
    }



    private static int prefZoom() {
        return prefs().getInt("zoom", 10);
    }

    private static void prefZoom(int zoom) {
        prefs().putInt("zoom", zoom);
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


    public void close() {
        // exit the application
        this.framer.close();
    }

    public void update(final Observable observable, final Object unused) {
        this.filer.updateMenu();
        this.framer.repaint();
    }

    private static class Ec implements Icon {
        private final float siz;
        private final int sign;
        private Ec(final float siz, int sign) {
            this.siz = siz;
            this.sign = sign;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int tx, int ty) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c.getForeground());
            g2d.translate(tx, ty);

            final float pc = this.siz/4.0f;
            final int p1 = toint(1.0f*pc);
            final int p2 = toint(2.0f*pc);
            final int p3 = toint(3.0f*pc);

            g2d.setColor(Color.MAGENTA);

            g2d.drawLine(p1, p2, p3, p2);
            if (0 < this.sign) {
                g2d.drawLine(p2, p1, p2, p3);
            }
            g2d.dispose();
        }

        @Override
        public int getIconWidth() {
            return toint(this.siz*2.0f);
        }

        @Override
        public int getIconHeight() {
            return toint(this.siz);
        }

        private static int toint(float v) {
            return Math.round(v);
        }
    }

    private void setUpUi() {
        useUiNamed("nimbus", this::setUiDefaults);
    }

    private void setUiDefaults(final UIDefaults defs) {
        defs.put("Tree.drawHorizontalLines", true);
        defs.put("Tree.drawVerticalLines", true);
        defs.put("Tree.linesStyle", "dashed");
        defs.put("Tree.closedIcon", null);
        defs.put("Tree.openIcon", null);
        defs.put("Tree.leafIcon", null);

        defs.put("Tree.rowHeight", visualSize *1.2);
        defs.put("Tree.font", new FontUIResource(new Font(Font.SANS_SERIF, Font.PLAIN, visualSize)));
        defs.put("Tree.expandedIcon", new Ec(visualSize, -1));
        defs.put("Tree.collapsedIcon", new Ec(visualSize, +1));
        defs.put("Tree.leftChildIndent", visualSize);
        defs.put("Tree.rightChildIndent", visualSize /2);
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
        appendFileMenuItems(menuFile);
        bar.add(menuFile);

        final JMenu menuView = new JMenu("View");
        menuView.setMnemonic(KeyEvent.VK_V);
        appendViewMenuItems(menuView);
        bar.add(menuView);

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
    private void appendViewMenuItems(final JMenu menu) {
        final JMenuItem itemZoomIn = new JMenuItem("Zoom in");
        itemZoomIn.setMnemonic(KeyEvent.VK_I);
        itemZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ACCEL));
        itemZoomIn.addActionListener(e -> zoom(+1));
        menu.add(itemZoomIn);
        final JMenuItem itemZoomOut = new JMenuItem("Zoom out");
        itemZoomOut.setMnemonic(KeyEvent.VK_O);
        itemZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ACCEL));
        itemZoomOut.addActionListener(e -> zoom(-1));
        menu.add(itemZoomOut);
    }

    private void appendHelpMenuItems(final JMenu menu) {
        final JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.setMnemonic(KeyEvent.VK_A);
        itemAbout.addActionListener(e -> about());
        menu.add(itemAbout);
    }

    private void zoom(final int i) {
        zoomContrained(4, i*(this.visualSize/16+1), 64);
        setUpUi();
        this.framer.updateUi();
    }

    private void zoomContrained(final int min, final int delta, final int max) {
        final int v = this.visualSize+delta;
        if (v < min) {
            this.visualSize = min;
        } else if (max < v) {
            this.visualSize = max;
        } else {
            this.visualSize = v;
        }
        prefZoom(this.visualSize);
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
