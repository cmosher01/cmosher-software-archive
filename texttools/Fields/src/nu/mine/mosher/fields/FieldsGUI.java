/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.fields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nu.mine.mosher.swingapp.SwingGUI;

/**
 * @author Chris Mosher
 */
public class FieldsGUI extends SwingGUI
{
    /**
     * @return image to use as the main frame's icon
     */
    protected Image getFrameIcon()
    {
        return new ImageIcon(this.getClass().getResource("appicon.gif")).getImage();
    }

    /**
     * @return menu bar for the application
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar mb = new JMenuBar();
        return mb;
    }

    /**
     * @return main content pane for the application
     */
    protected JPanel createContentPane()
    {
        JTable table = new JTable(new BigTableModel());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(80);
        }
        table.setPreferredScrollableViewportSize(new Dimension(640,480));

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel panel = new JPanel(new BorderLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();
        panel.add(scrollpane,BorderLayout.CENTER);

        return panel;
    }
}
