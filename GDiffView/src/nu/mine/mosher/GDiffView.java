/*
 * Created on Aug 27, 2004
 */
package nu.mine.mosher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import nu.mine.mosher.checksum.RollingChecksum;



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

    private File fileSrc;
    private File fileTrg;

    private InputStream dif;

    private int cCol = 0x10;

    private int nibs = 8;

    private int rowLen = nibs + 2 + 4 * cCol + 1;

    private StringBuffer sbSrc;
    private StringBuffer sbTrg;
    private long srcEOF;

    private long beginSrc;
    private long endSrc;

    private long beginTrg;
    private long endTrg;

    private JList listGDiff;

    List rCopy = new ArrayList();
    List rData = new ArrayList();



    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, BadLocationException, IOException, InvalidMagicBytes
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GDiffView frame = new GDiffView(new File(args[0]),new File(args[1]));
        frame.testMyDiff();
        frame.init();
    }

    /**
     * @throws IOException
     * 
     */
    private void testMyDiff() throws IOException
    {
        SourceFile src = new SourceFile();
        int cWindow = 3;
        src.calculateWindowChecksums(fileSrc,cWindow);
        InputStream streamTrg = new BufferedInputStream(new FileInputStream(fileTrg));
        if (streamTrg.available() == 0)
        {
            // TODO handle empty target file
            return;
        }
        byte[] rs = new byte[cWindow];
        RollingChecksum roll = new RollingChecksum();
        int trgPos = 0;
        List matches = new ArrayList();
        int w = cWindow;

        if (streamTrg.available() < cWindow)
        {
            w = streamTrg.available();
            rs = new byte[w];
        }
        int c = streamTrg.read(rs);
        if (c != w)
        {
            throw new IOException("error reading target file");
        }
        roll.init(rs);
        lookupUniqueMatch(src,roll.getChecksum(),trgPos,matches,w);
        trgPos += w;
        while (streamTrg.available() > 0)
        {
            byte x = (byte)streamTrg.read();
            byte xprev = roll(rs,x);
            roll.increment(xprev, x);
            lookupUniqueMatch(src,roll.getChecksum(),++trgPos,matches,w);
        }
    }

    /**
     * @param rs
     * @param x
     */
    private static byte roll(byte[] rs, byte x)
    {
        byte xk = rs[0];
        int e = rs.length-1;
        for (int i = 0; i < e; i++)
        {
            rs[i] = rs[i+1];
        }
        rs[e] = x;
        return xk;
    }

    /**
     * @param src
     * @param chk
     * @param trgPos
     * @param matches
     * @param w
     */
    private void lookupUniqueMatch(SourceFile src, int chk, int trgPos, List matches, int w)
    {
        long srcPos = src.lookupUnique(chk);
        if (srcPos >= 0)
        {
            Range rngSrc = new Range(srcPos,srcPos+w-1);
            Range rngTrg = new Range(trgPos,trgPos+w-1);
            GDiffCopy cpy = new GDiffCopy(rngSrc);
            cpy.setTargetRange(rngTrg);
//            matches.add(cpy);
            rCopy.add(cpy);
        }
    }

    public GDiffView(File fileSrc, File fileTrg) throws BadLocationException, IOException, InvalidMagicBytes
    {
        super("GDiffVeiew");

//        ByteArrayOutputStream rbs = Delta.delta(fileSrc, fileTrg);
//        dif = new ByteArrayInputStream(rbs.toByteArray());
        this.fileSrc = fileSrc;
        this.fileTrg = fileTrg;

        initStyles();



        JFrame.setDefaultLookAndFeelDecorated(true);
    }
    public void init() throws IOException, BadLocationException
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setSize(400,470);
        this.setLocation(10, 10);
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);

        docSrc = new DefaultStyledDocument();
        paneSrc = new JTextPaneNoWrap(docSrc);
        paneSrc.setEditable(false);
        JScrollPane scrSrc = new JScrollPane(paneSrc);
        scrSrc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrSrc.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        docTrg = new DefaultStyledDocument();
        paneTrg = new JTextPaneNoWrap(docTrg);
        paneTrg.setEditable(false);
        JScrollPane scrTrg = new JScrollPane(paneTrg);
        scrTrg.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrTrg.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        ListModel model = new GDiffCmdListModel(rCopy);
        listGDiff = new JList();
        listGDiff.setModel(model);
        listGDiff.setSelectionForeground(Color.BLACK);
        listGDiff.setSelectionBackground(new Color(173,194,245));
        listGDiff.setFont(Font.decode("Courier-PLAIN-10"));
        listGDiff.setPrototypeCellValue("@FFFFFFFF LFFFFFFFF");
        ListSelectionModel selectionModel = new SingleSelectionModel()
        {
            public void updateSingleSelection(int oldIndex, int newIndex)
            {
                if (oldIndex >= 0 && oldIndex < rCopy.size())
                {
                    GDiffCmd oldCmd = (GDiffCmd)rCopy.get(oldIndex);
                    if (oldCmd instanceof GDiffCopy)
                    {
                        GDiffCopy copy = (GDiffCopy)oldCmd;
                        beginSrc = copy.getRange().getBegin();
                        endSrc = copy.getRange().getEnd();
                        highlight("body",false);
                        beginTrg = copy.getTargetRange().getBegin();
                        endTrg = copy.getTargetRange().getEnd();
                        highlight("body",true);
                    }
                    else if (oldCmd instanceof GDiffData)
                    {
                        GDiffData data = (GDiffData)oldCmd;
                        beginTrg = data.getTargetRange().getBegin();
                        endTrg = data.getTargetRange().getEnd();
                        highlight("body",true);
                    }
                }
                if (newIndex >= 0 && newIndex < rCopy.size())
                {
                    GDiffCmd newCmd = (GDiffCmd)rCopy.get(newIndex);
                    if (newCmd instanceof GDiffCopy)
                    {
                        GDiffCopy copy = (GDiffCopy)newCmd;
                        beginSrc = copy.getRange().getBegin();
                        endSrc = copy.getRange().getEnd();
                        highlight("highlight",false);
                        beginTrg = copy.getTargetRange().getBegin();
                        endTrg = copy.getTargetRange().getEnd();
                        highlight("highlight",true);
                    }
                    else if (newCmd instanceof GDiffData)
                    {
                        GDiffData data = (GDiffData)newCmd;
                        beginTrg = data.getTargetRange().getBegin();
                        endTrg = data.getTargetRange().getEnd();
                        highlight("highlight",true);
                    }
                }
            }
        };
        JScrollPane scrGDiff = new JScrollPane(listGDiff);
        scrGDiff.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);



        SpringLayout layout = new SpringLayout();
        JPanel contentPane = new JPanel(layout);
        setContentPane(contentPane);

        SpringLayout.Constraints cnsDif = new SpringLayout.Constraints();
        cnsDif.setConstraint(SpringLayout.NORTH,layout.getConstraint(SpringLayout.NORTH,contentPane));

        cnsDif.setConstraint(SpringLayout.SOUTH,layout.getConstraint(SpringLayout.SOUTH,contentPane));
        cnsDif.setConstraint(SpringLayout.WEST,new SpringMid(layout.getConstraint(SpringLayout.EAST,contentPane),.5,listGDiff.getFixedCellWidth()+UIManager.getInt("ScrollBar.width")+3,false));
        cnsDif.setConstraint(SpringLayout.EAST,new SpringMid(layout.getConstraint(SpringLayout.EAST,contentPane),.5,listGDiff.getFixedCellWidth()+UIManager.getInt("ScrollBar.width")+3,true));

        SpringLayout.Constraints cnsSrc = new SpringLayout.Constraints();
        cnsSrc.setConstraint(SpringLayout.NORTH,layout.getConstraint(SpringLayout.NORTH,contentPane));
        cnsSrc.setConstraint(SpringLayout.SOUTH,layout.getConstraint(SpringLayout.SOUTH,contentPane));
        cnsSrc.setConstraint(SpringLayout.WEST,layout.getConstraint(SpringLayout.WEST,contentPane));
        cnsSrc.setConstraint(SpringLayout.EAST,cnsDif.getConstraint(SpringLayout.WEST));

        SpringLayout.Constraints cnsTrg = new SpringLayout.Constraints();
        cnsTrg.setConstraint(SpringLayout.NORTH,layout.getConstraint(SpringLayout.NORTH,contentPane));
        cnsTrg.setConstraint(SpringLayout.SOUTH,layout.getConstraint(SpringLayout.SOUTH,contentPane));
        cnsTrg.setConstraint(SpringLayout.EAST,layout.getConstraint(SpringLayout.EAST,contentPane));
        cnsTrg.setConstraint(SpringLayout.WEST,cnsDif.getConstraint(SpringLayout.EAST));

        contentPane.add(scrSrc,cnsSrc);
        contentPane.add(scrGDiff,cnsDif);
        contentPane.add(scrTrg,cnsTrg);

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
        docSrc.insertString(0,sbSrc.toString(),(AttributeSet)styles.get("body"));

//        readGDiff();
        tempReadTarget();
        docTrg.insertString(0,sbTrg.toString(),(AttributeSet)styles.get("body"));

        highlightInserts();
        highlightDeletes();

        listGDiff.setSelectionModel(selectionModel);
        listGDiff.setSelectedIndex(0);
        listGDiff.requestFocus();

        setVisible(true);
    }

    /**
     * 
     */
    private void initStyles()
    {
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

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style,"Courier");
        StyleConstants.setFontSize(style,10);
        StyleConstants.setBackground(style,new Color(120,194,120));
        StyleConstants.setForeground(style,Color.black);
        StyleConstants.setBold(style,false);
        StyleConstants.setItalic(style,false);
        styles.put("insert",style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style,"Courier");
        StyleConstants.setFontSize(style,10);
        StyleConstants.setBackground(style,new Color(194,120,120));
        StyleConstants.setForeground(style,Color.black);
        StyleConstants.setBold(style,false);
        StyleConstants.setItalic(style,false);
        styles.put("delete",style);
    }

    /**
     * 
     */
    protected void highlightDeletes()
    {
        SortedSet setSrcUsed = new TreeSet();
        for (Iterator i = rCopy.iterator(); i.hasNext();)
        {
            GDiffCopy g = (GDiffCopy)i.next();
            Range r = g.getRange();
            setSrcUsed.add(r);
        }

        SortedSet setSrcUnused = new TreeSet();
        long prev = 0;
        for (Iterator i = setSrcUsed.iterator(); i.hasNext();)
        {
            Range r = (Range)i.next();
            long next = r.getBegin();
            if (prev < next)
            {
                setSrcUnused.add(new Range(prev,next-1));
            }
            long lim = r.getLimit();
            if (prev < lim)
            {
                prev = lim;
            }
        }
        long next = this.srcEOF;
        if (prev < next)
        {
            setSrcUnused.add(new Range(prev,next-1));
        }
        for (Iterator i = setSrcUnused.iterator(); i.hasNext();)
        {
            Range r = (Range)i.next();
            beginSrc = r.getBegin();
            endSrc = r.getEnd();
            highlight("delete",false);
        }
    }

    /**
     * 
     */
    protected void highlightInserts()
    {
        for (Iterator i = rData.iterator(); i.hasNext();)
        {
            GDiffData g = (GDiffData)i.next();
            beginTrg = g.getTargetRange().getBegin();
            endTrg = g.getTargetRange().getEnd();
            highlight("insert",true);
        }
    }

    /**
     * @throws IOException
     */
    private void readSrc() throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileSrc));
        sbSrc = new StringBuffer(in.available() * 6);
        HexBuilder hex = new HexBuilder(sbSrc);
        hex.appendHeader();
        int x = in.read();
        while (x >= 0)
        {
            hex.appendByte(x);
            x = in.read();
        }
        hex.appendNewLine();
        in.close();
        srcEOF = hex.getAddr();
    }

    public void highlight(String highlight, boolean target)
    {
        long begin, end;
        JTextPane pan;
        if (target)
        {
            begin = beginTrg;
            end = endTrg;
            pan = paneTrg;
        }
        else
        {
            begin = beginSrc;
            end = endSrc;
            pan = paneSrc;
        }
        if (getRow(begin) == getRow(end))
        {
            long c = getHexStart(begin);
            highlight(c,getHexEnd(end),highlight,target);
            highlight(getAscStart(begin),getAscEnd(end),highlight,target);
            if (highlight.equals("highlight"))
            {
                pan.setCaretPosition((int)c);
            }
        }
        else
        {
            long c = getHexStart(begin);
            highlight(c,getHexRowEnd(getRow(begin)),highlight,target);
            highlight(getAscStart(begin),getAscRowEnd(getRow(begin)),highlight,target);
            for (long i = getRow(begin) + 1; i <= getRow(end) - 1; ++i)
            {
                highlight(getHexRowStart(i),getHexRowEnd(i),highlight,target);
                highlight(getAscRowStart(i),getAscRowEnd(i),highlight,target);
            }
            highlight(getHexRowStart(getRow(end)),getHexEnd(end),highlight,target);
            highlight(getAscRowStart(getRow(end)),getAscEnd(end),highlight,target);
            if (highlight.equals("highlight"))
            {
                pan.setCaretPosition((int)c);
            }
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

    public void highlight(long beginPoint, long endPoint, String highlight, boolean target)
    {
//        System.out.print("highlight: ");
//        System.out.print(target ? "TRG" : "SRC");
//        System.out.print("[");
//        System.out.print(beginPoint);
//        System.out.print("-");
//        System.out.print(endPoint);
//        System.out.print("] ");
//        System.out.println(highlight);
        AttributeSet attr = (AttributeSet)styles.get(highlight);
        if (target)
        {
            docTrg.setCharacterAttributes((int)beginPoint,(int)(endPoint - beginPoint),attr,true);
        }
        else
        {
            docSrc.setCharacterAttributes((int)beginPoint,(int)(endPoint - beginPoint),attr,true);
        }
    }

    public void readGDiff() throws IOException, InvalidMagicBytes
    {
        BufferedInputStream gdiff = new BufferedInputStream(dif);

        byte[] magic = new byte[4];
        gdiff.read(magic);
        validateMagic(magic);

        gdiff.read(); // ignore version

        RandomAccessFile in = new RandomAccessFile(fileSrc,"r");
        sbTrg = new StringBuffer((int)in.length()); // TODO long doesn't work

        HexBuilder hex = new HexBuilder(sbTrg);
        hex.appendHeader();

        GDiffCmd g = getGDiff(gdiff);
        long t = 0;
        while (!(g instanceof GDiffEnd))
        {
            byte[] rb;
            if (g instanceof GDiffData)
            {
                rData.add(g);
                GDiffData gd = (GDiffData)g;
                rb = gd.getData();
            }
            else
            {
                rCopy.add(g);
                GDiffCopy gc = (GDiffCopy)g;
                in.seek(gc.getRange().getBegin());
                rb = new byte[(int)gc.getRange().getLength()];
                in.readFully(rb);
            }

            long t0 = t;
            for (int i = 0; i < rb.length; i++)
            {
                ++t;
                hex.appendByte(rb[i]);
            }
            long t1 = t-1;
            g.setTargetRange(new Range(t0,t1));

            g = getGDiff(gdiff);
        }
        hex.appendNewLine();
        in.close();

//        listGDiff.setModel(new GDiffCmdListModel(rcmd)); // ???
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
     * @throws IOException
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
