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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
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
public class GDiffView extends JFrame
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

    private int rowLen = nibs + 2 + 4 * cCol + 1;

    private StringBuffer sb;
    private StringBuffer trg;

    private long beginSrc;

    private long endSrc;

    private JList listGDiff;

    List rcmd = new ArrayList();



    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException, BadLocationException, IOException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffView frame = new GDiffView(args[0],args[1]);
    }

    public GDiffView(String fileSrc, String fileGDiff) throws BadLocationException, IOException
    {
        super("GDiffVeiew");

        src = new File(fileSrc);
        dif = new File(fileGDiff);

        MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style,"Courier");
        StyleConstants.setFontSize(style,10);
        StyleConstants.setBackground(style,Color.white);
        StyleConstants.setForeground(style,Color.black);
        StyleConstants.setBold(style,false);
        StyleConstants.setItalic(style,false);
        styles.put("body",style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style,"Courier");
        StyleConstants.setFontSize(style,10);
        StyleConstants.setBackground(style,new Color(173,194,245));
        StyleConstants.setForeground(style,Color.black);
        StyleConstants.setBold(style,false);
        StyleConstants.setItalic(style,false);
        styles.put("highlight",style);

        docSrc = new DefaultStyledDocument();
        paneSrc = new JTextPaneNoWrap(docSrc);
        paneSrc.setEditable(false);
        JScrollPane scrSrc = new JScrollPane(paneSrc);
        scrSrc.setMinimumSize(new Dimension(100,100));
        scrSrc.setPreferredSize(new Dimension(400,430));
        scrSrc.setMaximumSize(new Dimension(5000,5000));

        docTrg = new DefaultStyledDocument();
        paneTrg = new JTextPaneNoWrap(docTrg);
        paneTrg.setEditable(false);
        JScrollPane scrTrg = new JScrollPane(paneTrg);
        scrTrg.setMinimumSize(new Dimension(100,100));
        scrTrg.setPreferredSize(new Dimension(400,430));
        scrTrg.setMaximumSize(new Dimension(5000,5000));

        rcmd.add(new GDiffCopy(new Range(0,1)));
        rcmd.add(new GDiffData(new byte[] {65, 67}));
        rcmd.add(new GDiffCopy(new Range(2,3)));
        rcmd.add(new GDiffCopy(new Range(1,4)));
        rcmd.add(new GDiffEnd());

        ListModel model = new GDiffCmdListModel(rcmd);
        listGDiff = new JList();
        listGDiff.setModel(model);
        listGDiff.setSelectionForeground(Color.BLACK);
        listGDiff.setSelectionBackground(new Color(173,194,245));
        ListSelectionModel selectionModel = new SingleSelectionModel()
        {
            public void updateSingleSelection(int oldIndex, int newIndex)
            {
                if (oldIndex >= 0)
                {
                    GDiffCmd oldCmd = (GDiffCmd)rcmd.get(oldIndex);
                    if (oldCmd instanceof GDiffCopy)
                    {
                        GDiffCopy copy = (GDiffCopy)oldCmd;
                        beginSrc = copy.getRange().getBegin();
                        endSrc = copy.getRange().getEnd();
                        highlight(false);
                    }
                }
                if (newIndex >= 0)
                {
                    GDiffCmd newCmd = (GDiffCmd)rcmd.get(newIndex);
                    if (newCmd instanceof GDiffCopy)
                    {
                        GDiffCopy copy = (GDiffCopy)newCmd;
                        beginSrc = copy.getRange().getBegin();
                        endSrc = copy.getRange().getEnd();
                        highlight(true);
                    }
                }
            }
        };
        JScrollPane scrGDiff = new JScrollPane(listGDiff);
        scrGDiff.setMinimumSize(new Dimension(300,460));
        scrGDiff.setPreferredSize(new Dimension(300,460));
        scrGDiff.setMaximumSize(new Dimension(300,460));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrSrc,BorderLayout.WEST);
        contentPane.add(scrGDiff,BorderLayout.CENTER);
        contentPane.add(scrTrg,BorderLayout.EAST);

        setContentPane(contentPane);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }

            public void windowActivated(WindowEvent e)
            {
                listGDiff.requestFocus();
            }
        });

        readSrc();
        docSrc.insertString(0,sb.toString(),(AttributeSet)styles.get("body"));

        listGDiff.setSelectionModel(selectionModel);
        listGDiff.setSelectedIndex(0);
        listGDiff.requestFocus();

        pack();

        setVisible(true);
    }

    /**
     * @throws IOException
     */
    private void readSrc() throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
        sb = new StringBuffer(in.available() * 6);
        HexBuilder hex = new HexBuilder(sb);
        hex.appendHeader();
        int x = in.read();
        while (x >= 0)
        {
            hex.appendByte(x);
            x = in.read();
        }
        hex.appendNewLine();
        in.close();
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
        appendHex(sb,(int)(addr >> 24));
        appendHex(sb,(int)(addr >> 16));
        appendHex(sb,(int)(addr >> 8));
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
        char hi = nib(i & 0xF);
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
            c = (char)(i + '0');
        }
        else
        {
            c = (char)(i - 10 + 'A');
        }
        return c;
    }

    //    public void highlightSrc(int begin, int end)
    //    {
    //        if (beginSrc >= 0 && endSrc >= 0)
    //        {
    //            highlight(false);
    //        }
    //        beginSrc = begin;
    //        endSrc = end;
    //        highlight(true);
    //    }

    public void highlight(boolean highlight)
    {
        if (getRow(beginSrc) == getRow(endSrc))
        {
            highlight(getHexStart(beginSrc),getHexEnd(endSrc),highlight);
            highlight(getAscStart(beginSrc),getAscEnd(endSrc),highlight);
        }
        else
        {
            highlight(getHexStart(beginSrc),getHexRowEnd(getRow(beginSrc)),highlight);
            highlight(getAscStart(beginSrc),getAscRowEnd(getRow(beginSrc)),highlight);
            for (long i = getRow(beginSrc) + 1; i <= getRow(endSrc) - 1; ++i)
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
    private long getAscRowStart(long row)
    {
        return (row + 1) * rowLen - 1 - cCol;
    }

    /**
     * @param row
     * @return
     */
    private long getAscRowEnd(long row)
    {
        return (row + 1) * rowLen - 1;
    }

    public long getRow(long pos)
    {
        return pos / cCol + 1;
    }

    public long getHexRowStart(long row)
    {
        return row * rowLen + nibs + 2;
    }

    public long getHexRowEnd(long row)
    {
        return (row + 1) * rowLen - 1 - cCol - 1;
    }

    public long getHexStart(long pos)
    {
        return rowLen + (pos / cCol) * rowLen + nibs + 2 + (pos % cCol) * 3;
    }

    public long getHexEnd(long pos)
    {
        return getHexStart(pos) + 2;
    }

    public long getAscStart(long pos)
    {
        return rowLen + (pos / cCol) * rowLen + nibs + 2 + cCol * 3 + (pos % cCol);
    }

    public long getAscEnd(long pos)
    {
        return getAscStart(pos) + 1;
    }

    public void highlight(long beginPoint, long endPoint, boolean highlight)
    {
        AttributeSet attr = (AttributeSet)styles.get(highlight ? "highlight" : "body");
        docSrc.setCharacterAttributes((int)beginPoint,(int)(endPoint - beginPoint),attr,true);
    }

    public void readGDiff() throws IOException, InvalidMagicBytes
    {
        BufferedInputStream gdiff = new BufferedInputStream(new FileInputStream(dif));

        byte[] magic = new byte[4];
        gdiff.read(magic);
        validateMagic(magic);

        gdiff.read(); // ignore version

        RandomAccessFile in = new RandomAccessFile(src,"r");

        PipedInputStream pipeIn = new PipedInputStream();
        Thread thTarg = new Thread(new Runnable()
        {
            public void run()
            {
                // TODO Auto-generated method stub
                
            }
    
        });
        thTarg.start();
        OutputStream pipeOut = new PipedOutputStream(pipeIn);

        GDiffCmd g = getGDiff(gdiff);
        trg = new StringBuffer(sb.length());
        while (!(g instanceof GDiffEnd))
        {
            if (g instanceof GDiffData)
            {
                GDiffData gd = (GDiffData)g;
            }
            else
            {
                GDiffCopy gc = (GDiffCopy)g;
                in.seek(gc.getRange().getBegin());
                byte[] rb = new byte[(int)gc.getRange().getLength()];
                in.readFully(rb);
                pipeOut.write(rb);
            }
            g = getGDiff(gdiff);
        }

        byte[] rb = new byte[cCol];
        int c = in.read(rb);
        sb = new StringBuffer(sb.length() * 6);
        for (int i = 0; i < nibs + 2; ++i)
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
     * @param magic
     * @throws InvalidMagicBytes
     */
    private static void validateMagic(byte[] magic) throws InvalidMagicBytes
    {
        if (
            magic.length != 4 ||
            magic[0] != (byte)0xD1 ||
            magic[1] != (byte)0xFF ||
            magic[2] != (byte)0xD1 ||
            magic[3] != (byte)0xFF)
        {
            throw new InvalidMagicBytes(magic);
        }
    }

    /**
     * @param gdiff
     * @return
     * @throws IO
     */
    private static GDiffCmd getGDiff(BufferedInputStream gdiff) throws IOException
    {
        int g = gdiff.read();
        if (g <= 0)
        {
            return new GDiffEnd();
        }
        if (g <= 246)
        {
            byte[] rb = new byte[g];
            gdiff.read(rb);
            return new GDiffData(rb);
        }
        if (g == 247)
        {
            int len = readShort(gdiff);
            byte[] rb = new byte[len];
            gdiff.read(rb);
            return new GDiffData(rb);
        }
        if (g == 248)
        {
            int len = readByte(gdiff);
            byte[] rb = new byte[len];
            gdiff.read(rb);
            return new GDiffData(rb);
        }
        if (g >= 256)
        {
            throw new RuntimeException("Byte value was 256 or greater.");
        }
        long pos;
        int len;
        switch (g)
        {
            case 249: pos = readShort(gdiff); len = readByte(gdiff); break;
            case 250: pos = readShort(gdiff); len = readShort(gdiff); break;
            case 251: pos = readShort(gdiff); len = readInt(gdiff); break;
            case 252: pos = readInt(gdiff); len = readByte(gdiff); break;
            case 253: pos = readInt(gdiff); len = readShort(gdiff); break;
            case 254: pos = readInt(gdiff); len = readInt(gdiff); break;
            case 255: pos = readLong(gdiff); len = readInt(gdiff); break;
            default: throw new RuntimeException("Can't happen.");
        }
        return new GDiffCopy(new Range(pos,pos+len-1));
    }

    /**
     * @param gdiff
     * @return
     * @throws IOException
     */
    private static int readByte(BufferedInputStream gdiff) throws IOException
    {
        return gdiff.read();
    }

    /**
     * @param gdiff
     * @return
     * @throws IOException
     */
    private static int readShort(BufferedInputStream gdiff) throws IOException
    {
        return readBytes(gdiff,2);
    }

    /**
     * @param gdiff
     * @return
     * @throws IOException
     */
    private static int readInt(BufferedInputStream gdiff) throws IOException
    {
        return readBytes(gdiff,4);
    }

    /**
     * @param gdiff
     * @return
     * @throws IOException
     */
    private static long readLong(BufferedInputStream gdiff) throws IOException
    {
        long i1 = readBytes(gdiff,4);
        long i0 = readBytes(gdiff,4);
        return (i1 << 32) | i0;
    }

    private static int readBytes(BufferedInputStream gdiff, int c) throws IOException
    {
        int n = 0;
        for (int i = 0; i < c; ++i)
        {
            n <<= 8;
            n |= gdiff.read();
        }
        return n;
    }
}
