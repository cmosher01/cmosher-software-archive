package nu.mine.mosher.plugin.history.actions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;



public class ExportHistory implements IObjectActionDelegate
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private ISelection currentSelection;
    private ZipOutputStream zip;



    public void run(IAction action)
    {
        try
        {
            Object o = ((IStructuredSelection) currentSelection).getFirstElement();
            IProject pr = (IProject)o;

            Shell shell = new Shell();
            FileDialog sa = new FileDialog(shell,SWT.SAVE);
            String szipfile = sa.open();
            if (szipfile == null)
            {
                return;
            }

            File fileZip = new File(szipfile);
            boolean okCreated = fileZip.createNewFile();
            if (!okCreated)
            {
                throw new IllegalStateException("Could not create that file.");
            }

            zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(fileZip)));

            processResources(pr);

            zip.flush();
            zip.close();

            MessageDialog.openInformation(shell,"History Exported","Local history exported.");
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            RuntimeException re = new IllegalStateException();
            re.initCause(e);
            throw re;
        }
    }

    /**
     * @param container
     * @throws CoreException
     */
    private void processResources(IContainer container) throws CoreException
    {
        IResource[] rr = container.members();
        for (int i = 0; i < rr.length; i++)
        {
            IResource resource = rr[i];
            IFile f = (IFile)resource.getAdapter(IFile.class);
            if (f != null)
            {
                putFileHistory(f);
            }
            else
            {
                IFolder dir = (IFolder)resource.getAdapter(IFolder.class);
                if (dir != null)
                {
                    processResources(dir);
                }
            }
        }
    }

    private void putFileHistory(IFile f) throws CoreException
    {
        IFileState[] rState = f.getHistory(null);
        for (int j = 0; j < rState.length; j++)
        {
            IFileState state = rState[j];
            putFileHistoryEntry(state);
        }
        // now put the current file's state, as the "latest" history entry
        putFileHistoryEntry(makeCurrentState(f));
        System.out.println("-----------------------------------------------");
    }

    private IFileState makeCurrentState(IFile f)
    {
        try
        {
            return new SimpleFileState(f.getFullPath(),f.getContents(),f.getLocalTimeStamp());
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void putFileHistoryEntry(IFileState state) throws CoreException
    {
        System.out.println(state.getClass().getName());
        String sZipEntryPath =
            state.getFullPath().makeRelative().toFile().getPath()+"_"+
            fmt.format(new Date(state.getModificationTime()))+".history";

        System.out.print("Writing ");
        System.out.println(sZipEntryPath);

        ZipEntry entry = new ZipEntry(sZipEntryPath);
        entry.setTime(state.getModificationTime());

        try
        {
            zip.putNextEntry(entry);
            BufferedInputStream cont = new BufferedInputStream(state.getContents());
            for (int b = cont.read(); b >= 0; b = cont.read())
            {
                zip.write(b);
            }
            cont.close();
            zip.closeEntry();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
