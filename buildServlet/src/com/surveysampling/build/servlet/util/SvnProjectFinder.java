/*
 * Created on Oct 25, 2005
 */
package com.surveysampling.build.servlet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.tmatesoft.svn.core.SVNException;



/**
 * Finds all Eclipse "projects" under a given Subversion root directory.
 * Not thread-safe.
 * @author Chris Mosher
 */
public class SvnProjectFinder
{
    private final Collection<String> rProject = new ArrayList<String>();
    private final SvnWrapper svn;

    /**
     * @param repository
     * @throws SVNException
     */
    public SvnProjectFinder(String repository) throws SVNException
    {
        this.svn = new SvnWrapper(repository);
    }

    /**
     * Do depth-first recursion from the given branch (a subversion URL),
     * stopping once we hit a .classpath file (indicating a project has
     * been found) or a normal file (so we don't recurse down past
     * where a normal file exists). Append all projects found to the given
     * Collection.
     * @param sBranch path within repository to start at
     * @param appendTo Collection to append projects to (with addAll method)
     * @throws SVNException 
     */
    public void getProjects(String sBranch, final Collection<String> appendTo) throws SVNException
    {
        if (!sBranch.endsWith("/"))
        {
            sBranch += "/";
        }

        this.rProject.clear();
        findProjects(sBranch);
        appendTo.addAll(this.rProject);
    }

    private void findProjects(final String dir) throws SVNException
    {
        final Collection<String> rsFile = Collections.synchronizedCollection(new ArrayList<String>());
        this.svn.list(dir,rsFile);

        boolean hasFile = false;
        final Collection<String> rsSubDir = new ArrayList<String>();
        for (final String nameFile : rsFile)
        {
            if (nameFile.equals(".classpath"))
            {
                /*
                 * We found a .classpath file in this directory,
                 * so we know this directory is actually an
                 * Eclipse project.
                 */
                this.rProject.add(dir);
                /*
                 * Therefore, we don't need to search any deeper, so
                 * end the recursion.
                 */
                return;
            }

            if (nameFile.endsWith("/"))
            {
                // a sub-directory we need to search
                rsSubDir.add(nameFile);
            }
            else
            {
                hasFile = true;
            }
        }

        if (hasFile)
        {
            /*
             * End the recursion when we find a directory that
             * has a file in it (as opposed to a directory that
             * only has sub-directories in it, in which case we
             * keep looking deeper).
             */
            return;
        }

        for (final String sSubDir : rsSubDir)
        {
            findProjects(dir+sSubDir);
        }
    }
}
