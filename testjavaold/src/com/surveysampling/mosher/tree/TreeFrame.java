package com.surveysampling.mosher.tree;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class TreeFrame extends JPanel implements WindowListener
{

    public TreeFrame(String dir)
    {
        setLayout(new BorderLayout());
        addNotify();
        setSize(getInsets().left + getInsets().right + 485, getInsets().top + getInsets().bottom + 367);
        setTitle("Java Directory Browser");

        FileTreeNode root = (FileTreeNode)buildTree(dir);
        DefaultTreeModel treemodel = new DefaultTreeModel(root);
        JTree tree = new JTree(treemodel);
        tree.setDoubleBuffered(true);

        HierarchyBrowserUpdater upd = new HierarchyBrowserUpdater(tree, root, treemodel, this);
        tree.addTreeExpansionListener(upd);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        scrollpane.setDoubleBuffered(true);

        add("Center", scrollpane);
        addWindowListener(this);
    }

    public TreeNode buildTree(String dir)
    {
        FileTreeNode root = new FileTreeNode(new File(dir));
        root.readTree(false);
        return (TreeNode)root;
    }

    public synchronized void setVisible(boolean show)
    {
        setLocation(50,50);
        super.setVisible(show);
    }

    public void windowClosed(WindowEvent event)
    {
    }

    public void windowDeiconified(WindowEvent event)
    {
    }

    public void windowIconified(WindowEvent event)
    {
    }

    public void windowActivated(WindowEvent event)
    {
    }

    public void windowDeactivated(WindowEvent event)
    {
    }

    public void windowOpened(WindowEvent event)
    {
    }

    public void windowClosing(WindowEvent event)
    {
        System.exit(0);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("FocusConceptsDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new FocusConceptsDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    public static void main(String argv[])
    {
        String dir = System.getProperty("user.dir");
        if (argv != null && argv.length >= 1)
        {
            dir = argv[0];
        }

        TreeFrame myframe = new TreeFrame(dir);
        myframe.setVisible(true);
    }
}
