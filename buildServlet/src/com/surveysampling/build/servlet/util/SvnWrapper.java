/*
 * Created on Oct 26, 2005
 */
package com.surveysampling.build.servlet.util;



import java.util.Collection;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.DefaultSVNRepositoryPool;



/**
 * A simple wrapper for JavaSVN, exposing just what we need
 * (dumbing down and cleaning up the interface).
 * 
 * @author Chris Mosher
 */
public class SvnWrapper
{
    private static final DefaultSVNRepositoryPool factoryRepository = new DefaultSVNRepositoryPool(new BasicAuthenticationManager("build","build"));



    private SVNRepository repository;

    /**
     * @param urlRepository 
     * @throws SVNException
     */
    public SvnWrapper(String urlRepository) throws SVNException
    {
        if (!urlRepository.endsWith("/"))
        {
            urlRepository += "/";
        }

        if (urlRepository.startsWith("http"))
        {
            DAVRepositoryFactory.setup();
        }
        else if (urlRepository.startsWith("svn"))
        {
            SVNRepositoryFactoryImpl.setup();
        }
        else
        {
            throw new IllegalArgumentException("Subversion URL not supported: "+urlRepository);
        }

        this.repository = SvnWrapper.factoryRepository.createRepository(SVNURL.parseURIEncoded(urlRepository),true);
    }

    /**
     * Lists the contents of the given directory path within
     * this repository.
     * @param path
     * @param rsFile
     * @throws SVNException
     */
    public void list(final String path, final Collection<String> rsFile) throws SVNException
    {
        final Collection<SVNDirEntry> rEntry = this.repository.getDir(path,-1,null,(Collection)null);
        for (final SVNDirEntry entry : rEntry)
        {
            String name = entry.getName();
            if (entry.getKind().equals(SVNNodeKind.DIR))
            {
                name = name+"/";
            }
            rsFile.add(name);
        }
    }

    /**
     * Gets the latest revision number of the repository.
     * This is just a pass-through to <code>SVNRepository.getLatestRevision()</code>.
     * @return the latest revision number
     * @throws SVNException
     */
    public long getLatestRevision() throws SVNException
    {
        return this.repository.getLatestRevision();
    }
}
