/**
 * @(#)Folder.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Folder is used to contains configuration pages. It has 2 buttons, 'Save'
 * and 'Cancel'. When 'Save' is clicked, it calls the parent callback function
 * to save the configuration parameters. It exits when 'Cancel' is clicked.
 */
class Folder extends Dialog implements ActionListener {

    private Vector PageHolder = new Vector();
    private Button save       = new Button("Save");
    private Button cancel     = new Button("Cancel");
    private SetupPage currentPage;
    private SetupPage defaultPage = new SetupPage("Empty");
    private TagPanel  tagPanel;
    private Panel bottomPanel;
    private Object parent = null;
    private Object controller = null;

    /**
     * constructor
     */
    public Folder(Frame p, Object c, String title) {
        this(p, title);
        controller = c;
    }

    /**
     * constructor
     */
    public Folder(Frame p, String title) {
        super(p, title, true); // force using modal mode
        parent = p;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK );
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK );
        setResizable(false);        // disable resizing

        // create and add tagPanel
        tagPanel = new TagPanel(this, 500, 30);
        this.add("North", tagPanel);

        // add SetupPage to the center
        currentPage = defaultPage; // initial page setup
        this.add("Center",currentPage);


        // create and add bottomPanel
        bottomPanel = new Panel();
        bottomPanel.add(save);
        save.addActionListener(this);
        bottomPanel.add(cancel);
        cancel.addActionListener(this);
        this.add("South", bottomPanel);

    }


    /**
     * add component
     */
    public Component add(Component c) { return c; } // override Dialog.add()

    /**
     * add a configuration page
     */
    public void addPage(SetupPage page) {
        PageHolder.addElement(page);
        this.remove(currentPage);
        currentPage = page;
        this.add("Center", currentPage);
        tagPanel.addTag(page.PageTitle);
    }

    /**
     * remove a configuration page
     */
    public void removePage(SetupPage page) {
        PageHolder.removeElement(page);
    }

    /**
     * return current number of pages
     */
    public int countPages() {
        return PageHolder.size();
    }

    /**
     * update a page
     */
    public void updateSetupPage(String pagetitle) {
        Enumeration e = PageHolder.elements();
        SetupPage  sp = null;

        // System.out.println("checking " + pagetitle );
        while( e.hasMoreElements() ) {
            sp = (SetupPage)e.nextElement();
            // System.out.println("in PageHolder " + sp.PageTitle );

            if( sp.PageTitle.equals(pagetitle) ) {
                this.remove(currentPage);
                currentPage = sp;
                this.add("Center", currentPage);
                validate();
                return;
            }
        }


    }

    public void update(Graphics g) { }

    /**
     * event handler for action events
     */
    public void actionPerformed(ActionEvent evt) {
        ConfigInterface si = null;
        if((evt.getActionCommand()).equals("Save")) {
            // need to save changes here
            if( controller != null ) si = (ConfigInterface) controller;
            else si = (ConfigInterface) parent;
            si.updateConfiguration();
            this.dispose();
        }

        if((evt.getActionCommand()).equals("Cancel")) {
            this.dispose();
        }


    } // end of public void actionPerformed(ActionEvent evt)

    /**
     * event handler for window events
     */
    public void processWindowEvent(WindowEvent winevt) {
        // check if the event is widow_destroy
        if( winevt.getID() == Event.WINDOW_DESTROY ) this.dispose();
    }

    /**
     * event handler for component events
     */
    public void processComponentEvent(ComponentEvent cmptevt) {
    }

} // end of class Folder
