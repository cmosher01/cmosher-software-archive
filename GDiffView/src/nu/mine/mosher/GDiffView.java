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

    private int nibs = 8;

    private int rowLen = nibs+2+4*cCol+1;

    private StringBuffer sb;

    private int srcBegin = -1;

    private int srcEnd = -1;
 
 
 
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
        doc.insertString(0,sb.toString(),(AttributeSet)styles.get("body"));
        highlightSrc(0,0x2E);
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
        for (int i = 0; i < cCol; ++i)
        {
            sb.append(' ');
        }
        long addr = 0;
        while (c > 0)
        {
            sb.append('\n');
            appendAddr(sb,addr);
            sb.append(": ");
            for (int i = 0; i < cCol; ++i)
            {
                if (i < c)
                {
                    appendHex(sb,rb[i]);
                }
                else
                {
                    sb.append("  ");
                }
                sb.append(' ');
            }
            for (int i = 0; i < cCol; ++i)
            {
                if (i < c)
                {
                    appendAsc(sb,rb[i]);
                }
                else
                {
                    sb.append(" ");
                }
            }
            addr += c;
            c = in.read(rb);
        }
        sb.append('\n');
        appendAddr(sb,addr);
        sb.append(": ");
    }

    /**
     * @param sb2
     * @param b
     */
    private static void appendAsc(StringBuffer sb, byte b)
    {
        if (b < 0)
        {
            b &= 0x7F;
        }
        if (32 <= b && b <= 126)
        {
            sb.append((char)b);
        }
        else
        {
            sb.append('.');
        }
    }

    /**
     * @param sb2
     * @param addr
     */
    private static void appendAddr(StringBuffer sb, long addr)
    {
        appendHex(sb,(int)(addr>>24));
        appendHex(sb,(int)(addr>>16));
        appendHex(sb,(int)(addr>>8));
        appendHex(sb,(int)(addr));
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

    public void highlightSrc(int begin, int end)
    {
        if (srcBegin >= 0 && srcEnd >= 0)
        {
            highlight(false);
        }
        srcBegin = begin;
        srcEnd = end;
        highlight(true);
    }
    public void highlight(boolean highlight)
    {
        if (getRow(srcBegin)==getRow(srcEnd))
        {
            highlight(getHexStart(srcBegin),getHexEnd(srcEnd),highlight);
            highlight(getAscStart(srcBegin),getAscEnd(srcEnd),highlight);
        }
        else
        {
            highlight(getAscStart(srcBegin),getAscRowEnd(getRow(srcBegin)),highlight);
            for (int i = getRow(srcBegin)+1; i <= getRow(srcEnd)-1; ++i)
            {
                highlight(getAscRowStart(i),getAscRowEnd(i),highlight);
            }
            highlight(getAscRowStart(getRow(srcEnd)),getAscEnd(srcEnd),highlight);
        }
    }
    /**
     * @param i
     * @return
     */
    private int getAscRowStart(int row)
    {
        return (row+1)*rowLen-1-cCol;
    }

    /**
     * @param row
     * @return
     */
    private int getAscRowEnd(int row)
    {
        return (row+1)*rowLen-1;
    }

    public int getRow(int pos)
    {
        return pos/cCol+1;
    }
    public int getHexRowStart(int row)
    {
        return row*rowLen+nibs+2;
    }
    public int getHexRowEnd(int row)
    {
        return (row+1)*rowLen-1-cCol-1;
    }
    public int getHexStart(int pos)
    {
        return rowLen+(pos/cCol)*rowLen+nibs+2+(pos%cCol)*3;
    }
    public int getHexEnd(int pos)
    {
        return getHexStart(pos)+2;
    }
    public int getAscStart(int pos)
    {
        return rowLen+(pos/cCol)*rowLen+nibs+2+cCol*3+(pos%cCol);
    }
    public int getAscEnd(int pos)
    {
        return getAscStart(pos)+1;
    }
    public void highlight(int beginPoint, int endPoint, boolean highlight)
    {
        AttributeSet attr = (AttributeSet)styles.get(highlight ? "highlight": "body");
        doc.setCharacterAttributes(beginPoint,endPoint-beginPoint,attr,true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException, BadLocationException, IOException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffVeiew frame = new GDiffVeiew(args[0],args[1]);
//        Thread.sleep(5000);
    }
}
