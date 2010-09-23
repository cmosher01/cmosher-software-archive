/**
 * 
 */
package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.callproc.SpeakerSheet;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author parkerj
 *@author sharmar
 *
 */

public class PressSpeakerGroupTalkCommandImpl extends PressSpeakerGroupTalkCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
        switch (this.getGroup())
        {
            case GROUP_1:
            {
                speakerSheet.pressGroupPtt(1);
                dsUpdate();
            }
            break;
            case GROUP_2:
            {
                speakerSheet.pressGroupPtt(2);
                dsUpdate();
            }
            break;
        }
        
        return new Nothing<ExecutionResult>();
    }
    
    private void dsUpdate() throws ExecutionException
    {
        try
        {
            /*
             * Getting Speaker Channel from DS
             */
            final UserSpeakerChannelManager spkrChnlMgr = new UserSpeakerChannelManager(this.ctx.getSecurityContext());
            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            final List<UserSpeakerChannel> dsUsrSprList = spkrChnlMgr.getUserSpeakerChannelsFor(usrCDI);
            UserSpeakerChannel usrSprChl = null;
            if(dsUsrSprList != null && !dsUsrSprList.isEmpty())
            { 
                for (UserSpeakerChannel userSpeakerChannel : dsUsrSprList)
                {
                    usrSprChl = userSpeakerChannel;
                    
                    switch(this.getGroup())
                    {
                        case GROUP_1:
                            usrSprChl.setIsInGroup1(usrSprChl.getIsInGroup1());
                            break;
                        case GROUP_2:
                            usrSprChl.setIsInGroup2(usrSprChl.getIsInGroup2());
                            break;
                    }
                }
                /*
                 * save Speaker Group  
                 */
                spkrChnlMgr.save(usrSprChl);
            }       
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(
                    "Unable to update speaker group number "
                            + this.getGroup() + " for user: " + this.ctx.getUserName());
        }
    }

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }
}
