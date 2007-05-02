/*
 * Created on Oct 25, 2005
 */
package com.surveysampling.build.servlet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.tmatesoft.svn.core.SVNException;



/**
 * Finds all branches in a given Subversion repository.
 * Branch refers to a directory within the repository that
 * contains Eclipse projects. It could either be the trunk,
 * a tag, or a branch; in Subversion terms, they are just
 * directories within the repository. Not thread-safe.
 * 
 * @author Chris Mosher
 */
public class SvnBranchFinder
{
    private final Collection<String> rBranch = new ArrayList<String>();
    private final SvnWrapper svn;

    /**
     * @param repository
     * @throws SVNException
     */
    public SvnBranchFinder(String repository) throws SVNException
    {
        this.svn = new SvnWrapper(repository);
    }

    /**
     * Do depth-first recursion from the given root (a subversion URL),
     * stopping once we hit a .classpath file (indicating a branch has
     * been found) or a normal file (so we don't recurse down past
     * where a normal file exists).
     * @param appendTo
     * @throws SVNException 
     */
    public void getBranches(final Collection<String> appendTo) throws SVNException
    {
        this.rBranch.clear();
        hasBranch("/");
        appendTo.addAll(this.rBranch);
    }

    private boolean hasBranch(final String dir) throws SVNException
    {
        final Collection<String> rsFile = Collections.synchronizedCollection(new ArrayList<String>());
        this.svn.list(dir,rsFile);

        boolean hasFile = false;
        final Collection<String> rsSubDir = new ArrayList<String>();
        for (final String nameFile : rsFile)
        {
            if (nameFile.equals(".classpath"))
            {
                // we found it, so take a quick exit
                return true;
            }

            if (nameFile.endsWith("/"))
            {
                rsSubDir.add(nameFile);
            }
            else
            {
                hasFile = true;
            }
        }

        if (hasFile)
        {
            // end the recursion when we found a directory that
            // has a file in it (as opposed to a directory that
            // only has sub-directories in it).
            return false;
        }

        for (final String sSubDir : rsSubDir)
        {
            if (hasBranch(dir+sSubDir))
            {
                this.rBranch.add(dir);
                return false; // quick exit
            }
        }
        // we only get here when there is an
        // empty directory (I think)
        return false;
    }
}
