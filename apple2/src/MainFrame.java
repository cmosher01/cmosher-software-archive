import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
/*
 * Created on Oct 16, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class MainFrame extends JFrame
{
    private JTree tree;
    private DefaultMutableTreeNode top = new DefaultMutableTreeNode("Program");



    /**
     * @param args
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     * @throws IOException
     * @throws InvalidPosException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InvalidPosException, IOException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MainFrame frame = new MainFrame();
        frame.init();
    }

    /**
     * @param disk
     * @throws InvalidPosException
     */
    private void doOneDisk(Disk disk) throws InvalidPosException
    {
        Volume vol = new Volume();
        try
        {
            vol.readFromMedia(disk);
        }
        catch (VTOCNotFoundException e)
        {
            System.out.println("[no VTOC]");
            return;
        }
        catch (MultipleVTOCException e)
        {
            System.out.println("[no VTOC]");
            return;
        }

        StringBuffer s = new StringBuffer();

        DefaultMutableTreeNode nDisk = new DefaultMutableTreeNode("DOS33_SystemMaster_19830101.dsk");
//        DefaultMutableTreeNode nDisk = new DefaultMutableTreeNode("DiversiDOS_2_C_1982.dsk");
        DefaultMutableTreeNode nBoot = new DefaultMutableTreeNode("Bootstrap Loader");
        s.append("APPLE ][ DOS 3.3");
        vol.getDos().appendSig(s);
        DefaultMutableTreeNode nDos = new DefaultMutableTreeNode(s);

        DefaultMutableTreeNode nCat = new DefaultMutableTreeNode("Catalog");

        DefaultMutableTreeNode nFiles = new DefaultMutableTreeNode("Files");
        List rFiles = new ArrayList();
        vol.getFiles(rFiles);
        for (Iterator i = rFiles.iterator(); i.hasNext();)
        {
            VolumeFile file = (VolumeFile)i.next();
            nFiles.add(new DefaultMutableTreeNode(file.getCatalogEntry().getName()));
        }

        DefaultMutableTreeNode nFilesRecovered = new DefaultMutableTreeNode("Recovered Files");
        List rFilesRecovered = new ArrayList();
        vol.getFilesRecovered(rFilesRecovered);
        for (Iterator i = rFilesRecovered.iterator(); i.hasNext();)
        {
            VolumeFileRecovered file = (VolumeFileRecovered)i.next();
            List rPos = new ArrayList();
            file.getTSMap().getPos(rPos);
            VolumeSector vs = (VolumeSector)rPos.get(0);
            nFilesRecovered.add(new DefaultMutableTreeNode("recovered @ "+vs.getPos().toStringTS()));
        }

        DefaultMutableTreeNode nOrphaned = new DefaultMutableTreeNode("Orphaned Data");

        DefaultMutableTreeNode nBlank = new DefaultMutableTreeNode("Blank Sectors");

        DefaultMutableTreeNode nMap = new DefaultMutableTreeNode("Track/Sector Map");

        nDisk.add(nBoot);
        nDisk.add(nDos);
        nDisk.add(nCat);
        if (!rFiles.isEmpty())
        {
            nDisk.add(nFiles);
        }
        if (!rFilesRecovered.isEmpty())
        {
            nDisk.add(nFilesRecovered);
        }
        nDisk.add(nOrphaned);
        nDisk.add(nBlank);
        nDisk.add(nMap);

        top.add(nDisk);
    }

    /**
     * @throws java.awt.HeadlessException
     */
    public MainFrame() throws HeadlessException
    {
        super("Apple ][ DOS 3.3 Diskette Image Analyzer");
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     * 
     */
    private void init() throws InvalidPosException, IOException
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setSize(400,470);
        this.setLocation(10, 10);
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);

        doOneDisk(Apple2.readDisk(new File("test/DOS33_SystemMaster_19830101.dsk")));
//        doOneDisk(Apple2.readDisk(new File("test/DiversiDOS_2_C_1982.dsk")));

        tree = new JTree(top);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollTree.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        JSplitPane split = new JSplitPane();
        split.setLeftComponent(scrollTree);
        split.setDividerLocation(320);
        split.setPreferredSize(new Dimension(640,480));

        setContentPane(split);

        setVisible(true);
    }
}
