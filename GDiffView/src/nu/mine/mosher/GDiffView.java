/*
 * Created on Aug 27, 2004
 */
package nu.mine.mosher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private File src;

    private File dif;

    private int cCol = 0x10;

    private StringBuffer sb;
 
 
 
    public GDiffVeiew(String fileSrc, String fileGDiff) throws BadLocationException, IOException
    {
        super("GDiffVeiew");

        src = new File(fileSrc);
        dif = new File(fileGDiff);

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

        readSrc();
        doc.insertString(0,sb.toString(),
//            "          00 01 02 03 04 05 06 07          \n"+
//            "00000000: D1 FF D1 FF 04 01 20 30  MWMWMWMW\n"+
//            "00000008: D1 FF D1 FF 04 01 20 30  ilililil\n",
        (AttributeSet)styles.get("body"));
 
//        doc.setCharacterAttributes(57,5,(AttributeSet)styles.get("highlight"),true);                                                                 
//        doc.setCharacterAttributes(80,2,(AttributeSet)styles.get("highlight"),true);                                                                 
 
        pack();
        setVisible(true);
    }
 
    /**
     * @throws IOException
     * 
     */
    private void readSrc() throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
        byte[] rb = new byte[cCol];
        int c = in.read(rb);
        int nibs = 8;
        sb = new StringBuffer(in.available()*6);
        for (int i = 0; i < nibs+2; ++i)
        {
            sb.append(' ');
        }
        for (int i = 0; i < cCol; ++i)
        {
            appendHex(sb,i);
            sb.append(' ');
        }
        for (int i = 0; i < cCol+2; ++i)
        {
            sb.append(' ');
        }
        sb.append('\n');
        while (c > 0)
        {
            c = in.read(rb);
        }
    }

    /**
     * @param sb2
     * @param i
     */
    private static void appendHex(StringBuffer sb, int i)
    {
        char lo = nib(i & 0xF);
        i >>= 4;
        char hi = nib(i);
        sb.append(hi);
        sb.append(lo);
    }

    /**
     * @param i
     * @return
     */
    private static char nib(int i)
    {
        char c;
        if (i < 10)
        {
            c = (char)(i+'0');
        }
        else
        {
            c = (char)(i-10+'A');
        }
        return c;
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException, BadLocationException, IOException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffVeiew frame = new GDiffVeiew(args[0],args[1]);
//        Thread.sleep(5000);
    }
}
