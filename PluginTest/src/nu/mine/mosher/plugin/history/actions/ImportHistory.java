package nu.mine.mosher.plugin.history.actions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.internal.localstore.FileSystemResourceManager;
import org.eclipse.core.internal.localstore.HistoryStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;



public class ImportHistory implements IObjectActionDelegate
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private ISelection currentSelection;
    private IWorkspace workspace;
    private HistoryStore historyStore;



    public void run(IAction action)
    {
        try
        {
            workspace = ResourcesPlugin.getWorkspace();
            FileSystemResourceManager fileSystemManager = ((org.eclipse.core.internal.resources.Workspace)workspace).getFileSystemManager();
            historyStore = (HistoryStore)fileSystemManager.getHistoryStore();

            Object o = ((IStructuredSelection) currentSelection).getFirstElement();
            IProject pr = (IProject)o;

            Shell shell = new Shell();
            FileDialog sa = new FileDialog(shell,SWT.OPEN);
            String szipfile = sa.open();
            if (szipfile == null)
            {
                return;
            }

            File fileZip = new File(szipfile);
            ZipFile zf = new ZipFile(fileZip);

            for (Enumeration i = zf.entries(); i.hasMoreElements();)
            {
                ZipEntry entry = (ZipEntry)i.nextElement();
                InputStream cont = zf.getInputStream(entry);
                processEntry(pr,entry,cont);
                cont.close();
            }

            zf.close();

            workspace.save(true, null);

            MessageDialog.openInformation(shell,"History Imported","Local history imported.");
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            RuntimeException re = new IllegalStateException();
            re.initCause(e);
            throw re;
        }
    }

    private void processEntry(IProject proj, ZipEntry entry, InputStream cont)
    {
        String n = entry.getName();
        if (!n.endsWith(".history"))
        {
            System.err.println("Skipping input file: "+n);
            return;
        }

        String sPath = n.substring(0, n.length()-26);
        String sTime = n.substring(n.length()-25,n.length()-8);
        long tim = 0;
        try
        {
            tim = fmt.parse(sTime).getTime();
        }
        catch (ParseException e)
        {
            RuntimeException ex = new IllegalStateException("Invalid time string: "+sTime);
            ex.initCause(e);
            throw ex;
        }

        IResource found = workspace.getRoot().findMember(sPath);
        if (found == null)
        {
            System.err.println("Can't find: "+sPath);
            return;
        }
        IFile fil = (IFile)found.getAdapter(IFile.class);
        if (fil != null)
        {
            System.err.println("Reading: "+n);
            if (!proj.equals(fil.getProject()))
            {
                throw new IllegalStateException("Project in imported zip file is not the same as the project chosen to import into.");
            }

            try
            {
                File osFile = fil.getLocation().toFile();
                osFile.delete();
                osFile.createNewFile();
                BufferedOutputStream otf = new BufferedOutputStream(new FileOutputStream(osFile),8192);
                BufferedInputStream inf = new BufferedInputStream(cont,8192);
                for (int b = inf.read(); b >= 0; b = inf.read())
                {
                    otf.write(b);
                }
                otf.close();
                osFile.setLastModified(tim);

                historyStore.addState(fil.getFullPath(),osFile,tim,false);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        this.currentSelection = selection;
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }
}
