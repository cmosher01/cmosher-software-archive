/*
 * Created on Aug 27, 2004
 */
package nu.mine.mosher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
 
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * TODO
 * 
 * @author Chris
 */
public class GDiffVeiew extends JFrame
{
    private StyledDocument doc;
 
    private JTextPane pane;
   
    private Map styles = new HashMap();
 
 
 
    public GDiffVeiew() throws BadLocationException
    {
        super("GDiffVeiew");
 
        doc = new DefaultStyledDocument();
        pane = new JTextPane(doc);
        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setPreferredSize(new Dimension(620,460));
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrollPane,BorderLayout.CENTER);
        setContentPane(contentPane);
 
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
 
            public void windowActivated(WindowEvent e)
            {
                // focus magic
                pane.requestFocus();
            }
        });
 
        MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier");
        StyleConstants.setFontSize(style, 10);
        StyleConstants.setBackground(style, Color.white);
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        styles.put("body", style);
 
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier");
        StyleConstants.setFontSize(style, 10);
        StyleConstants.setBackground(style, new Color(173,194,245));
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        styles.put("highlight", style);
 
        doc.insertString(0,
            "          00 01 02 03 04 05 06 07          \n"+
            "00000000: D1 FF D1 FF 04 01 20 30  MWMWMWMW\n"+
            "00000008: D1 FF D1 FF 04 01 20 30  ilililil\n",
        (AttributeSet)styles.get("body"));
 
        doc.setCharacterAttributes(57,5,(AttributeSet)styles.get("highlight"),true);                                                                 
        doc.setCharacterAttributes(80,2,(AttributeSet)styles.get("highlight"),true);                                                                 
 
        pack();
        setVisible(true);
    }
 
    public static void main(String[] args) throws BadLocationException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffVeiew frame = new GDiffVeiew();
        Thread.sleep(5000);
    }
}
