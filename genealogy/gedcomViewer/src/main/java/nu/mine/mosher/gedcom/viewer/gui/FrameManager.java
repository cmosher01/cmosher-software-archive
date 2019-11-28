/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.gui;

import nu.mine.mosher.gedcom.viewer.GedcomViewer;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.Closeable;
import java.io.File;
import java.util.Optional;



public class FrameManager implements Closeable {
    private final GedcomTreeModel model;
    private JFrame frame;

    public FrameManager(final GedcomTreeModel model) {
        this.model = model;
    }

    public void init(final JMenuBar bar, final Runnable onClose) {
        // Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call "onClose" runnable
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                onClose.run();
            }
        });

        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("GEDCOM Viewer");

        this.frame.setJMenuBar(bar);

        // Create and set up the content pane.
        this.frame.setContentPane(new GedcomTreePane(this.model));

        this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.frame.getLayeredPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);

    }

    private static File dir() {
        return new File(GedcomViewer.prefs().get("dir", "./"));
    }

    private static void dir(final File dir) {
        GedcomViewer.prefs().put("dir", dir.getAbsolutePath());
    }

    public File getFileToOpen() throws UserCancelled {
        final JFileChooser chooser = new JFileChooser(dir());
        final int actionType = chooser.showOpenDialog(this.frame);
        if (actionType != JFileChooser.APPROVE_OPTION) {
            throw new UserCancelled();
        }

        dir(chooser.getCurrentDirectory());

        return chooser.getSelectedFile();
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this.frame, message);
    }

    public void close() {
        this.frame.dispose();
    }


    private static Image getFrameIcon() {
        final BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        final String text = "G";
        final Rectangle rect = new Rectangle(200, 200);

        final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 128);

        final Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.decode("0xfdf6e3"));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(Color.decode("0xd33682"));
        g.setFont(font);

        final FontMetrics metrics = g.getFontMetrics(font);
        final int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        final int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(text, x, y);
        return bufferedImage;
    }

    public static class UserCancelled extends Exception {
        private UserCancelled() {
        }
    }
}
