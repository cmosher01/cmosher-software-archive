/**
 * 
 */
package com.ipc.uda.service.servlet.mock.event.linestatus;

import com.ipc.uda.service.servlet.mock.event.monitoring.MockMonitorEvent;
import com.ipc.va.cti.lineStatus.events.LineStatusEvent;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.dialog.Dialog;
import com.ipc.va.dialog.DialogInfo;

/**
 * @author mordarsd
 *
 */
public class MockLineStatusEvent extends MockMonitorEvent implements LineStatusEvent
{
    
    private AgentID agentID;
    private Dialog dialog;
    private DialogInfo dialogInfo;

    

    /**
     * 
     */
    public MockLineStatusEvent()
    {
        super();
    }


    /* (non-Javadoc)
     * @see com.ipc.va.cti.lineStatus.events.LineStatusEvent#getAgentID()
     */
    public AgentID getAgentID()
    {
        return agentID;
    }


    public Dialog getDialog()
    {
        return dialog;
    }
    
    public DialogInfo getDialogInfo()
    {
        return dialogInfo;
    }


    public void setAgentID(AgentID agentID)
    {
        this.agentID = agentID;
    }


    public void setDialog(Dialog dialog)
    {
        this.dialog = dialog;
    }


    public void setDialogInfo(DialogInfo dialogInfo)
    {
        this.dialogInfo = dialogInfo;
    }
    
    
}
