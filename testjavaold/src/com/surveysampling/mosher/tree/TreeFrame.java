package com.surveysampling.mosher.tree;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.surveysampling.mosher.Flag;

public class TreeFrame extends JPanel
{
    private static Flag begun = new Flag();

    public TreeFrame(String dir)
    {
        setLayout(new BorderLayout());
        addNotify();
        setSize(getInsets().left + getInsets().right + 485, getInsets().top + getInsets().bottom + 367);

        FileTreeNode root = (FileTreeNode)buildTree(dir);
        DefaultTreeModel treemodel = new DefaultTreeModel(root);
        JTree tree = new JTree(treemodel);

        HierarchyBrowserUpdater upd = new HierarchyBrowserUpdater(tree, root, treemodel, this);
        tree.addTreeExpansionListener(upd);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);

        add("Center", scrollpane);
    }

    public TreeNode buildTree(String dir)
    {
        FileTreeNode root = new FileTreeNode(new File(dir));
        root.readTree(false);
        return (TreeNode)root;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    protected static void createAndShowGUI(String dir) throws InterruptedException
    {
        useOSLookAndFeel();
        //Make sure we have nice window decorations.
        //JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Java Directory Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JPanel newContentPane = new TreeFrame(dir);
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setLocation(50,50);
        frame.setVisible(true);

        begun.waitToSetTrue();
    }

    protected static void useOSLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable ignoreAnyExceptions)
        {
        }
    }

    public static void main(String argv[]) throws Throwable
    {
        String dir = System.getProperty("user.dir");
        if (argv != null && argv.length >= 1)
        {
            dir = argv[0];
        }
        final String dirArg = dir;

        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    createAndShowGUI(dirArg);
                }
                catch (Throwable th)
                {
                    th.printStackTrace();
                }
            }
        });

        begun.waitUntilTrue();
    }
}
