package com.surveysampling.beans.editors;

import java.beans.PropertyEditorManager;
import java.util.Arrays;
import java.util.LinkedList;

/**
 */
public final class Editors
{
    private Editors()
    {
        throw new UnsupportedOperationException();
    }

    public static void register()
    {
        String[] rp = PropertyEditorManager.getEditorSearchPath();
        LinkedList listp = new LinkedList(Arrays.asList(rp));

        listp.addFirst(Editors.class.getPackage().getName());

        PropertyEditorManager.setEditorSearchPath((String[])listp.toArray(new String[listp.size()]));
    }
}
