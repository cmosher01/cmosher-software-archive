import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

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
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MainFrame frame = new MainFrame();
        frame.init();
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
     * 
     */
    private void init()
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setSize(400,470);
        this.setLocation(10, 10);
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);

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
