package nu.mine.mosher.beans.editors;

import java.beans.PropertyEditorManager;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Contains static methods pertaining to JavaBean PropertyEditor classes.
 */
public final class Editors
{
    private Editors()
    {
        throw new UnsupportedOperationException();
    }

	/**
	 * Registers this package in the search path for
	 * PropertyEditorManager. This package is prepended
	 * to the list of existing paths, so that editors in
	 * this package take precedence over exiting editors.
	 */
    public static void register()
    {
        String[] rp = PropertyEditorManager.getEditorSearchPath();
        LinkedList listp = new LinkedList(Arrays.asList(rp));

        listp.addFirst(Editors.class.getPackage().getName());

        PropertyEditorManager.setEditorSearchPath((String[])listp.toArray(new String[listp.size()]));
    }
}
