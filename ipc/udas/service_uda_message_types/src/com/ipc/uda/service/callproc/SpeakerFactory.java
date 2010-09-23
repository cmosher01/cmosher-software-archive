package com.ipc.uda.service.callproc;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonAppearance;

/**
 * @author sharmar
 *
 */
public class SpeakerFactory
{

    /**
     * Creates a UdaSpeaker from a DataServices com.ipc.ds.entity.dto.UserSpeakerChannel
     * @param usrSpkrChl 
     * @param userCtx 
     * @return 
     * @throws InvalidContextException 
     * @throws StorageFailureException 
     * 
     */
    
    public static Optional<UdaSpeaker> create(final UserSpeakerChannel usrSpkrChl, final UserContext userCtx)
        throws InvalidContextException, StorageFailureException
    {
      final ResourceAORManager mgrAor = new ResourceAORManager(userCtx.getSecurityContext());
      final ResourceAOR aor = mgrAor.getResourceAORFor(usrSpkrChl);
   
      final ButtonAppearance aorAndAppearance = ButtonFactory.createAppearance(aor,usrSpkrChl.getAppearance());
      
      final UdaSpeaker udaSpeaker = new UdaSpeaker(usrSpkrChl,aorAndAppearance);
      switch(aor.getType())
      {
          case Private:
          case Dialtone:
          {
              udaSpeaker.setCall(new SpeakerCall(userCtx,aorAndAppearance,udaSpeaker,aor.getInitiateCallOnSeize(),aor.getType()));
              return new Optional<UdaSpeaker>(udaSpeaker);
          }

          default:
          {
              Log.logger().info("Unsupported AOR type for speaker ("+aor.getType()+"); ignoring this speaker.");
              return new Nothing<UdaSpeaker>();
          }
      }
    }
}
