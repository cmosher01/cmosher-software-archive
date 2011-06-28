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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
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
        frame.init();
//        RollingChecksum c = new RollingChecksum();
//        byte[] rb = new byte[] { (byte)0xA4, (byte)0xC2, (byte)0xEF, (byte)0xCE, (byte)0x48, (byte)0x21,
//                (byte)0x51, (byte)0x9B, (byte)0x50, (byte)0xAB, (byte)0xF9, (byte)0x7A, (byte)0xE6, (byte)0xE3, (byte)0xDB, (byte)0x92, (byte)0xBA, (byte)0xC6, (byte)0x42, (byte)0x65, (byte)0xE0, (byte)0xAA,
//                (byte)0x4E, (byte)0x08, (byte)0x10, (byte)0xBF, (byte)0x4C, (byte)0x3F, (byte)0xCD, (byte)0xAF, (byte)0x47, (byte)0x7C, (byte)0xFF, (byte)0x00, (byte)0xEA, (byte)0xE9, (byte)0x22, (byte)0xC0,
//                (byte)0xDC, (byte)0x29, (byte)0x27, (byte)0x97, (byte)0xEC, (byte)0x0D, (byte)0x27, (byte)0xE4, (byte)0x37, (byte)0x9C, (byte)0xAE, (byte)0x94, (byte)0x42, (byte)0x5F, (byte)0x93, (byte)0xFE,
//                (byte)0x6D, (byte)0xD9, (byte)0x4E, (byte)0x18, (byte)0xE2, (byte)0x7E, (byte)0x81, (byte)0x02, (byte)0x20, (byte)0x1C, (byte)0x93 };
//        c.init(rb);
//        int chk1 = c.getChecksum();
//        rb = new byte[] { (byte)0xAD, (byte)0x2C, (byte)0xB4, (byte)0xB7, (byte)0x9A,
//                (byte)0x66, (byte)0x5B, (byte)0x11, (byte)0xD0, (byte)0xE8, (byte)0x63, (byte)0xEA, (byte)0xCC, (byte)0x0A, (byte)0x7C, (byte)0x64, (byte)0x4E, (byte)0x76, (byte)0xD3, (byte)0xF0, (byte)0xDA,
//                (byte)0x57, (byte)0xD0, (byte)0x0F, (byte)0xBF, (byte)0x1C, (byte)0xEE, (byte)0xB4, (byte)0xD9, (byte)0x60, (byte)0x5D, (byte)0x93, (byte)0x93, (byte)0x01, (byte)0xBA, (byte)0x7C, (byte)0xE9,
//                (byte)0x4C, (byte)0xD9, (byte)0x48, (byte)0xF0, (byte)0x3C, (byte)0x51, (byte)0x98, (byte)0x3E, (byte)0xF1, (byte)0x30, (byte)0x87, (byte)0xF5, (byte)0xC4, (byte)0x0F, (byte)0xE5, (byte)0x7B,
//                (byte)0x57, (byte)0x96, (byte)0x5A, (byte)0x74, (byte)0xC0, (byte)0x19, (byte)0x8D, (byte)0xDC, (byte)0x42, (byte)0x44, (byte)0x33, (byte)0x1B };
//        c.init(rb);
//        int chk2 = c.getChecksum();

    }

//    /**
//     * @throws IOException
//     */
//    private void initDiff() throws IOException
//    {
//        SourceFile src = new SourceFile();
//        int cWindow = 5;
//        src.calculateWindowChecksums(fileSrc,cWindow);
//        InputStream streamTrg = new BufferedInputStream(new FileInputStream(fileTrg));
//        if (streamTrg.available() == 0)
//        {
//            // TODO handle empty target file
//            return;
//        }
//        byte[] rs = new byte[cWindow];
//        RollingChecksum roll = new RollingChecksum();
//
//        int trgPos = 0;
//        int w = cWindow;
//
//        if (streamTrg.available() < cWindow)
//        {
//            w = streamTrg.available();
//            rs = new byte[w];
//        }
//        int c = streamTrg.read(rs);
//        if (c != w)
//        {
//            throw new IOException("error reading target file");
//        }
//        roll.init(rs);
//        lookupUniqueMatch(src,roll.getChecksum(),trgPos++,w);
//        while (streamTrg.available() > 0)
//        {
//            byte x = (byte)streamTrg.read();
//            byte xprev = roll(rs,x);
//            roll.increment(xprev, x);
//            lookupUniqueMatch(src,roll.getChecksum(),trgPos++,w);
//        }
//        consolidateCopies();
//    }
//
//    /**
//     * 
//     */
//    private void consolidateCopies()
//    {
//        int a = 0;
//        int b = 1;
//        while (b < rCopy.size())
//        {
//            GDiffCopy cpya = (GDiffCopy)rCopy.get(a);
//            Range rngSrca = cpya.getRange();
//            Range rngTrga = cpya.getTargetRange();
//            GDiffCopy cpyb = (GDiffCopy)rCopy.get(b);
//            Range rngSrcb = cpyb.getRange();
//            Range rngTrgb = cpyb.getTargetRange();
//            Range rngCombSrc = Range.meld(rngSrca, rngSrcb);
//            Range rngCombTrg = Range.meld(rngTrga, rngTrgb);
//            if (rngCombSrc != null && rngCombTrg != null)
//            {
//                GDiffCopy cpyComb = new GDiffCopy(rngCombSrc);
//                cpyComb.setTargetRange(rngCombTrg);
//                rCopy.remove(b);
//                rCopy.set(a,cpyComb);
//            }
//            else
//            {
//                ++a;
//                ++b;
//            }
//        }
//    }
//
//    /**
//     * @param rs
//     * @param x
//     * @return
//     */
//    private static byte roll(byte[] rs, byte x)
//    {
//        byte xk = rs[0];
//        int e = rs.length-1;
//        for (int i = 0; i < e; i++)
//        {
//            rs[i] = rs[i+1];
//        }
//        rs[e] = x;
//        return xk;
//    }
//
//    /**
//     * @param src
//     * @param chk
//     * @param trgPos
//     * @param w
//     */
//    private void lookupUniqueMatch(SourceFile src, int chk, int trgPos, int w)
//    {
//        long srcPos = src.lookupUnique(chk);
//        if (srcPos >= 0)
//        {
//            Range rngSrc = new Range(srcPos,srcPos+w-1);
//            Range rngTrg = new Range(trgPos,trgPos+w-1);
//            GDiffCopy cpy = new GDiffCopy(rngSrc);
//            cpy.setTargetRange(rngTrg);
////            matches.add(cpy);
//            rCopy.add(cpy);
//        }
//    }
//
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
                        Range target = copy.getTargetRange();
                        if (target != null)
                        {
                            beginTrg = target.getBegin();
                            endTrg = target.getEnd();
                            highlight("body",true);
                        }
                    }
                    else if (oldCmd instanceof GDiffData)
                    {
                        GDiffData data = (GDiffData)oldCmd;
                        Range target = data.getTargetRange();
                        if (target != null)
                        {
                            beginTrg = target.getBegin();
                            endTrg = target.getEnd();
                            highlight("body",true);
                        }
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
                        Range target = copy.getTargetRange();
                        if (target != null)
                        {
                            beginTrg = target.getBegin();
                            endTrg = target.getEnd();
                            highlight("highlight",true);
                        }
                    }
                    else if (newCmd instanceof GDiffData)
                    {
                        GDiffData data = (GDiffData)newCmd;
                        Range target = data.getTargetRange();
                        if (target != null)
                        {
                            beginTrg = target.getBegin();
                            endTrg = target.getEnd();
                            highlight("highlight",true);
                        }
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

        svndelta();

        initText();
        initHighlights();

        listGDiff.setSelectionModel(selectionModel);
        listGDiff.setSelectedIndex(0);
        listGDiff.requestFocus();

        setVisible(true);
    }

    public void initText() throws IOException, BadLocationException
    {
        readSrc();

        StringBuffer sb = new StringBuffer((int)srcEOF);
        tempReadTarget(sb);
        docTrg.insertString(0,sb.toString(),(AttributeSet)styles.get("body"));
    }

    public void initHighlights()
    {
        highlightInserts();
        highlightDeletes();
    }

    /**
     * @throws IOException
     * 
     */
    private void tempReadTarget(StringBuffer sb) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileTrg));
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
            Range target = g.getTargetRange();
            if (target != null)
            {
                beginTrg = target.getBegin();
                endTrg = target.getEnd();
                highlight("insert",true);
            }
        }
    }

    /**
     * @throws IOException
     * @throws BadLocationException
     */
    private void readSrc() throws IOException, BadLocationException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileSrc));
        StringBuffer sbSrc = new StringBuffer(in.available() * 6);
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
        docSrc.insertString(0,sbSrc.toString(),(AttributeSet)styles.get("body"));
    }

    /**
     * @param highlight
     * @param target
     */
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
                try
                {
                    pan.setCaretPosition((int)c);
                }
                catch (IllegalArgumentException ex)
                {
                    // TODO
                }
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
                try
                {
                    pan.setCaretPosition((int)c);
                }
                catch (IllegalArgumentException ex)
                {
                    // TODO
                }
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
        StringBuffer sbTrg = new StringBuffer((int)in.length()); // TODO long doesn't work

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

    private final int cWindow = 17;
    private final Map map = new HashMap(0x8000,.5f);

    /**
     * @param args
     * @throws IOException
     */
    public void svndelta() throws IOException
    {
        FileInputStream streamSrc = new FileInputStream(this.fileSrc);
        FileInputStream streamTrg = new FileInputStream(this.fileTrg);
        FileChannel channelSrc = streamSrc.getChannel();
        FileChannel channelTrg = streamTrg.getChannel();
        long cSrc = streamSrc.available();
        long cTrg = streamTrg.available();
        ByteBuffer data = ByteBuffer.allocate((int)(cSrc+cTrg)); // TODO
        channelSrc.read(data);
        channelTrg.read(data);
        vdelta(data,cSrc,cTrg);
    }

    /**
     * @param data
     * @param cSrc
     * @param cTrg
     */
    public void vdelta(ByteBuffer data, long cSrc, long cTrg)
    {
        data.position(0);
        data.limit((int)cSrc); // TODO
        vdeltaPrivate(data,false);

        data.position((int)cSrc);
        data.limit((int)(cSrc+cTrg)); // TODO
        vdeltaPrivate(data,true);
    }
    private void vdeltaPrivate(ByteBuffer data, boolean output)
    {
        long start = data.position();
        long end = data.limit();
        long here = start;
        long insertFrom = -1;
        long trgpos = 0;
        while (true)
        {
            if (end-here < cWindow)
            {
                long from;
                if (insertFrom < 0)
                {
                    from = here;
                }
                else
                {
                    from = insertFrom;
                }
                if (output && from < end)
                {
                    byte[] rb = new byte[(int)(end-from)];
                    data.position((int)from); // TODO long
                    data.get(rb);
                    doData(rb,new Range(trgpos,trgpos+end-from-1));
                    trgpos += end-from;
                }
                return;
            }

            long matchCurrent = -1;
            long cMatchCurrent = 0;
            long key = here;
            boolean progress;
            do
            {
                progress = false;
                Collection vals = (Collection)map.get(getBucket(data,key));
                if (vals != null)
                {
                    for (Iterator i = vals.iterator(); i.hasNext();)
                    {
                        long there = ((Long)i.next()).longValue();
                        if (there < key-here)
                        {
                            continue;
                        }
                        long match = there-(key-here);
                        long cMatch = extendMatch(data,match,here);
                        if (match < start && match+cMatch > start)
                        {
                            cMatch = start-match;
                        }
                        if (cMatch >= cWindow)
                        {
//                            if (cMatch == cMatchCurrent)
//                            {
//                                System.out.print("same len  "+cMatch);
//                                System.out.print(" candidate src: "+Long.toHexString(match));
//                                System.out.print(" prev cand src: "+Long.toHexString(matchCurrent));
//                                System.out.print(" target       : "+Long.toHexString(here));
//                                if (here-match < here-matchCurrent)
////                                long corresp = there+start;
////                                long thatdif = match;
////                                if (thatdif < 0)
////                                {
////                                    thatdif = -thatdif;
////                                }
////                                long thisdif = matchCurrent-corresp;
////                                if (thisdif < 0)
////                                {
////                                    thisdif = -thisdif;
////                                }
////                                System.out.print("that: "+Long.toHexString(thatdif));
////                                System.out.print(" this: "+Long.toHexString(thisdif));
////                                if (thatdif < thisdif)
//                                {
//                                    System.out.print("          using: "+Long.toHexString(match));
//                                    matchCurrent = match;
//                                    cMatchCurrent = cMatch;
//                                    progress = true;
//                                }
//                                System.out.println();
//                            }
                            if (cMatch >= cMatchCurrent)
                            {
                                matchCurrent = match;
                                cMatchCurrent = cMatch;
                                progress = true;
                            }
                        }
                    }
                    if (progress)
                    {
                        key = here+cMatchCurrent-cWindow+1;
                    }
                }
            }
            while (progress & end-key >= cWindow);

            if (cMatchCurrent < cWindow)
            {
                if (!output)
                {
                    addMapping(data,here);
                }
                if (insertFrom < 0)
                {
                    insertFrom = here;
                }
                ++here;
                continue;
            }
            else if (output)
            {
                if (insertFrom >= 0)
                {
                    byte[] rb = new byte[(int)(here-insertFrom)];
                    data.position((int)insertFrom); // TODO long
                    data.get(rb);
                    doData(rb,new Range(trgpos,trgpos+here-insertFrom-1));
                    trgpos += here-insertFrom;
                    insertFrom = -1;
                }
                doCopy(matchCurrent,cMatchCurrent,(start < matchCurrent),new Range(trgpos,trgpos+cMatchCurrent-1));
                trgpos += cMatchCurrent;
            }
            here += cMatchCurrent;
            if (end-here >= cWindow && !output)
            {
                for (long last = here-cWindow+1; last < here; ++last)
                {
                    addMapping(data,last);
                }
            }
        }
    }

    /**
     * @param data
     * @param where
     */
    private void addMapping(ByteBuffer data, long where)
    {
        Object key = getBucket(data,where);
        Collection vals = (Collection)map.get(key);
        if (vals == null)
        {
            vals = new ArrayList();
            map.put(key, vals);
        }
        vals.add(new Long(where));
    }

    /**
     * @param rb
     * @return
     */
    private Object hash(byte[] rb)
    {
        int hash = 0;
        for (int i = 0; i < rb.length; i++)
        {
            hash *= 127;
            hash += rb[i];
        }
        return new Integer(hash); // TODO mod?
    }

    /**
     * @param data
     * @param match
     * @param from
     * @return
     */
    private long extendMatch(ByteBuffer data, long match, long from)
    {
        long here = from;
        while (here < data.limit() && data.get((int)match) == data.get((int)here))
        {
            ++match;
            ++here;
        }
        return here - from;
    }

    /**
     * @param data
     * @param key
     * @return
     */
    private Object getBucket(ByteBuffer data, long key)
    {
        data.position((int)key); // TODO
        byte[] rb = new byte[cWindow];
        data.get(rb);
        return hash(rb);
    }

    /**
     * @param data
     */
    private void doData(byte[] data, Range target)
    {
        GDiffData cmd = new GDiffData(data);
        cmd.setTargetRange(target);
        this.rData.add(cmd);
    }

    /**
     * @param offset
     * @param length
     * @param fromTarget
     */
    private void doCopy(long offset, long length, boolean fromTarget, Range target)
    {
        if (fromTarget)
        {
            throw new IllegalStateException();
        }
        GDiffCopy cmd = new GDiffCopy(new Range(offset,offset+length-1));
        cmd.setTargetRange(target);
        this.rCopy.add(cmd);
    }
}
