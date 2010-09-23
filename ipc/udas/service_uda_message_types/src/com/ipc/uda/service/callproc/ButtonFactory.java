/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;



import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonResourceAppearance;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.manager.ButtonResourceAppearanceManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.ButtonAppearance;

/**
 * @author mordarsd
 * @author mosherc
 */
public final class ButtonFactory
{

    /**
     * Creates a UdaButton from a DataServices com.ipc.ds.entity.dto.Button
     * 
     * @param dsButton
     * @return a new {@link UdaButton} corresponding to <code>dsbutton</code>
     * @throws StorageFailureException 
     * @throws InvalidContextException 
     */
    public static UdaButton create(final Button dsButton, final UserContext userCtx)
        throws InvalidContextException, StorageFailureException
    {
        switch (dsButton.getButtonType())
        {
            case Resource:                  
            case ResourceAndSpeedDial:       
                return createResourceButton(dsButton,userCtx);

            case PersonalPointOfContact:
            case PointOfContact:
            case DuplexConference:
            case OneButtonICMDivert:
            case HuntAndSpeedDial:
            case SpeedDial:
            case MWI:
            case ICM:
            case OneButtonDivert:
            case KeySequence:
            	return createFunctionalButton(dsButton);
            case SimplexConference:
            default:
                return createNullButton(dsButton);
        }
    }
    
    
    private static UdaButton createFunctionalButton(Button dsButton) {
    	return createNullButton(dsButton);
	}


	private static UdaButton createResourceButton(final Button dsButton, final UserContext userCtx)
        throws InvalidContextException, StorageFailureException
    {
        final ButtonResourceAppearanceManager mgrAppearance = new ButtonResourceAppearanceManager(userCtx.getSecurityContext());
        for (final ButtonResourceAppearance appearance : mgrAppearance.getResourcesFor(dsButton))
        {
            final ResourceAORManager mgrAor = new ResourceAORManager(userCtx.getSecurityContext());
            final ResourceAOR aor = mgrAor.getResourceAORFor(appearance);
            final ButtonAppearance aorAndAppearance = createAppearance(aor,appearance.getAppearance());
            switch (aor.getType())
            {
                case Private:
                {
                    final CallBasedButton udaButton = new CallBasedButton(
                        dsButton,
                        aorAndAppearance);
                    
                    udaButton.setCall(
                        new PrivateWireCall(
                            userCtx,aorAndAppearance, 
                            udaButton,
                            aor.getInitiateCallOnSeize()));
                    
                    return udaButton;
                }

                case Dialtone:
                {
                    final CallBasedButton udaButton = new CallBasedButton(
                        dsButton,
                        aorAndAppearance);
                    
                    udaButton.setCall(
                        new PrivateWireCall(
                            userCtx,aorAndAppearance, 
                            udaButton,
                            aor.getInitiateCallOnSeize()));
                    
                    return udaButton;
                }
                    
                case OOPS:
                case OpenConnexion:
                    return createNullButton(dsButton);
            }
        }
        return createNullButton(dsButton);
    }

    /**
     * Given ResourceAOR and ButtonResourceAppearance DS entities, construct
     * a ButtonAppearance value-bean containing just the AOR and the appearance.
     * @param res
     * @param btnResAppearance
     * @return
     * @throws StorageFailureException
     */
    public static ButtonAppearance createAppearance(
        final ResourceAOR res,
        final int btnResAppearance)
        throws StorageFailureException
    {
        try
        {
            return new ButtonAppearance(res.getResourceAOR(),btnResAppearance);
        }
        catch (final Throwable e)
        {
            throw (StorageFailureException)new StorageFailureException().initCause(e);
        }
    }

    @SuppressWarnings("synthetic-access")
    private static UdaButton createNullButton(final Button dsButton)
    {
        return new NullButton(dsButton);
    }



    public static class NullButton extends UdaButton
    {
        private NullButton(final Button button)
        {
            super(button);
        }

        @Override
        public void press()
        {
            throw new UnsupportedOperationException("No implementation found for ButtonType: " + this.getDsButton().getButtonType());
        }

        @Override
        public Optional<ButtonAppearance> getAppearance()
        {
            return new Nothing<ButtonAppearance>();
        }

        @Override
        public Optional<ButtonPressCall> getCall()
        {
            return new Nothing<ButtonPressCall>();
        }
    }
}
