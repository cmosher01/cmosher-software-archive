package com.surveysampling.mosher.tree;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileTreeNode extends DefaultMutableTreeNode
{
    boolean mRead;

    public FileTreeNode(File file)
    {
        super(file);
    }

    public boolean readTree()
    {
        return readTree(false);
    }

    public boolean readTree(boolean b)
    {
        if (mRead)
        {
            return false;
        }

        String list[] = getFileObject().list();
        if (list != null)
        {
            for (int i = 0; i < list.length; i++)
            {
                FileTreeNode subnode = new FileTreeNode(new File(getFileObject(), list[i]));
                add(subnode);
                if (b)
                    subnode.readTree(b);
            }
        }
        mRead = true;

        return true;
    }

    public File getFileObject()
    {
        return (File)getUserObject();
    }

    public String toString()
    {
        return getFileObject().getName();
    }

    public boolean isLeaf()
    {
        return ((File)userObject).isFile();
    }
}
