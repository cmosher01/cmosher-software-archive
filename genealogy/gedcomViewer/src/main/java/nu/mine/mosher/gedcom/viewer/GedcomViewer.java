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
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

public class GedcomViewer implements Runnable, Closeable, Observer {
    /**
     * @param args
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main(final String... args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                final Runnable program = new GedcomViewer();
                program.run();
            }
        });
    }

    private final GedcomTreeModel model = new GedcomTreeModel();
    private final FrameManager framer = new FrameManager(this.model);
    private final FileManager filer = new FileManager(this.model, this.framer);

    private GedcomViewer() {
        // instantiated by main method
    }



    public void run() {
        this.model.addObserver(this);

        setLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        // Must be done before creating any JFrame or JDialog
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        setDefaultFont();

        // create the application's menu bar
        final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);

        // create the main frame window for the application
        this.framer.init(menubar, new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                close();
            }
        });

        update(this.model, null);
    }


    public void close() {
        this.framer.close(); // this exits the app
        System.gc();
        System.err.println("end");
    }

    public void update(final Observable observable, final Object unused) {
        this.filer.updateMenu();
        this.framer.repaint();
    }



    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setDefaultFont() {
        /*
         * Use Java's platform independent font, Lucida Sans, plain, at 12 points,
         * as the default for every Swing component.
         */

        final FontUIResource font = new FontUIResource("Lucida Sans", Font.PLAIN, 14);

        final Enumeration<Object> iterKeys = UIManager.getDefaults().keys();
        while (iterKeys.hasMoreElements()) {
            final Object key = iterKeys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    private void appendMenus(final JMenuBar bar) {
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        this.filer.appendMenuItems(menuFile);
        menuFile.addSeparator();
        appendMenuItems(menuFile);

        bar.add(menuFile);
    }

    private void appendMenuItems(final JMenu menu) {
        final JMenuItem itemFileExit = new JMenuItem("Exit");
        itemFileExit.setMnemonic(KeyEvent.VK_X);
        itemFileExit.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                close();
            }
        });
        menu.add(itemFileExit);
    }
}
