package com.surveysampling.mosher.tree;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;

import com.surveysampling.mosher.Flag;

public class TreeFrame extends JPanel
{
    public TreeFrame()
    {
        String dir = System.getProperty("user.dir");

        setLayout(new BorderLayout());
        setOpaque(true);
        addNotify();

        FileTreeNode root = buildTree(dir);
        DefaultTreeModel treemodel = new DefaultTreeModel(root);

        JTree tree = new JTree(treemodel);
        tree.addTreeExpansionListener(new HierarchyBrowserUpdater(treemodel));

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);

        add("Center", scrollpane);
    }

    public FileTreeNode buildTree(String dir)
    {
        FileTreeNode root = new FileTreeNode(new File(dir));
        root.readTree(false);
        return root;
    }

    protected static void createAppFrame()
    {
        useOSLookAndFeel();

        //Create and set up the window.
        JFrame frame = new JFrame("Java Directory Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        frame.setContentPane(createContentPane());

        //Display the window.
        frame.pack();
        frame.setLocation(50,50);
        frame.setVisible(true);
    }

    protected static Container createContentPane()
    {
        return new TreeFrame();
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
        final Flag begun = new Flag();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    createAppFrame();
                    begun.waitToSetTrue();
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
