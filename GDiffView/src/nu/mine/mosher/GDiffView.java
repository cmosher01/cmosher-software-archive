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
    private StyledDocument docSrc;
 
    private JTextPane paneSrc;
   
    private StyledDocument docTrg;
    
   private JTextPane paneTrg;

   private Map styles = new HashMap();

    private File src;

    private File dif;

    private int cCol = 0x10;

    private int nibs = 8;

    private int rowLen = nibs+2+4*cCol+1;

    private StringBuffer sb;

    private int beginSrc = -1;

    private int endSrc = -1;
 
 
 
    public GDiffVeiew(String fileSrc, String fileGDiff) throws BadLocationException, IOException
    {
        super("GDiffVeiew");

        src = new File(fileSrc);
        dif = new File(fileGDiff);

        docSrc = new DefaultStyledDocument();
        paneSrc = new JTextPane(docSrc);
        paneSrc.setEditable(false);
        JScrollPane scrSrc = new JScrollPane(paneSrc);
        scrSrc.setPreferredSize(new Dimension(620,460));

        docTrg = new DefaultStyledDocument();
        paneTrg = new JTextPane(docTrg);
        paneTrg.setEditable(false);
        JScrollPane scrTrg = new JScrollPane(paneTrg);
        scrTrg.setPreferredSize(new Dimension(620,460));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrSrc,BorderLayout.CENTER);
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
                paneSrc.requestFocus();
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
        docSrc.insertString(0,sb.toString(),(AttributeSet)styles.get("body"));
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
     * @param sb
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
     * @param sb
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
     * @param sb
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
        if (beginSrc >= 0 && endSrc >= 0)
        {
            highlight(false);
        }
        beginSrc = begin;
        endSrc = end;
        highlight(true);
    }
    public void highlight(boolean highlight)
    {
        if (getRow(beginSrc)==getRow(endSrc))
        {
            highlight(getHexStart(beginSrc),getHexEnd(endSrc),highlight);
            highlight(getAscStart(beginSrc),getAscEnd(endSrc),highlight);
        }
        else
        {
            highlight(getHexStart(beginSrc),getHexRowEnd(getRow(beginSrc)),highlight);
            highlight(getAscStart(beginSrc),getAscRowEnd(getRow(beginSrc)),highlight);
            for (int i = getRow(beginSrc)+1; i <= getRow(endSrc)-1; ++i)
            {
                highlight(getHexRowStart(i),getHexRowEnd(i),highlight);
                highlight(getAscRowStart(i),getAscRowEnd(i),highlight);
            }
            highlight(getHexRowStart(getRow(endSrc)),getHexEnd(endSrc),highlight);
            highlight(getAscRowStart(getRow(endSrc)),getAscEnd(endSrc),highlight);
        }
    }
    /**
     * @param row
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
        docSrc.setCharacterAttributes(beginPoint,endPoint-beginPoint,attr,true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException, BadLocationException, IOException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffVeiew frame = new GDiffVeiew(args[0],args[1]);
        frame.highlightSrc(3,0x06);
        Thread.sleep(2000);
        frame.highlightSrc(4,0x16);
        Thread.sleep(2000);
        frame.highlightSrc(5,0x26);
    }
}
