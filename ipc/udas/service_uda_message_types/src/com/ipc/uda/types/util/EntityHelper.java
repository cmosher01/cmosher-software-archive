/**
 * 
 */
package com.ipc.uda.types.util;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.uda.types.ButtonType;
import com.ipc.uda.types.ImmutableContactType;
import com.ipc.uda.types.UserCdiType;

/**
 * 
 * Helper for creating one UDA/DS entity type from another
 * 
 * @author mordarsd
 *
 */
public final class EntityHelper
{

    /**
     * 
     * 
     * @param userCDI
     * @return
     */
    public static UserCdiType fromDSUserCDI(final UserCDI userCDI)
    {
        final UserCdiType target = new UserCdiType();
        Converters.DSUserCDIConverter.convert(userCDI,target);
        return target;
    }
    
    /**
     * Converts DS Button entity to UDA ButtonType entity
     * @param dsButton Button
     * @return ButtonType target
     */
    public static ButtonType fromDSButton(final Button dsButton)
    {
        final ButtonType target = new ButtonType();
        Converters.DSButtonConverter.convert(dsButton,target);
        return target;
    }
    
    /**
     * Converts DS Contact entity to ImmutableContactType entity
     * @param dsContact Contact
     * @return ImmutableContactType target
     */
    public static ImmutableContactType fromDSContactToImmutableContact(final Contact dsContact)
    {
        final ImmutableContactType target = new ImmutableContactType();
        Converters.DSContactConverter.convert(dsContact, target);
        return target;
    }

    /**
     * Converts DS PersonalContact entity to ImmutableContactType entity
     * @param dsContact PersonalContact
     * @return ImmutableContactType target
     */
    public static ImmutableContactType fromDSPersonalContactToImmutableContact(final PersonalContact dsContact)
    {
        final ImmutableContactType target = new ImmutableContactType();
        Converters.DSPersonalContactConverter.convert(dsContact, target);
        return target;
    }
}
